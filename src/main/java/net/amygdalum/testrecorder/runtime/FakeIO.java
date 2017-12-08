package net.amygdalum.testrecorder.runtime;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static net.amygdalum.testrecorder.asm.ByteCode.argumentTypesFrom;
import static net.amygdalum.testrecorder.runtime.GenericObject.copyArrayValues;
import static net.amygdalum.testrecorder.runtime.GenericObject.copyField;
import static net.amygdalum.testrecorder.util.Types.allFields;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import net.amygdalum.testrecorder.asm.ByteCode;
import net.amygdalum.testrecorder.bridge.BridgedFakeIO;
import net.amygdalum.testrecorder.util.Types;
import net.bytebuddy.agent.ByteBuddyAgent;

public class FakeIO {

	public static final Object NO_RESULT = new Object();

	private static FakeIOTransformer fakeIOTransformer = install();
	private static Map<String, FakeIO> faked = new HashMap<>();

	private Class<?> clazz;
	private List<Interaction> interactions;

	private FakeIO(Class<?> clazz) {
		this.clazz = clazz;
		this.interactions = new ArrayList<>();
	}

	public static Object callFake(String name, StackTraceElement[] stackTrace, Object instance, String methodName, String methodDesc, Object... varargs) {
		if (isRecording(stackTrace)) {
			return NO_RESULT;
		}
		FakeIO fake = faked.get(name);
		if (fake == null) {
			return NO_RESULT;
		}
		Invocation invocation = Invocation.capture(stackTrace, instance, fake.clazz, methodName, methodDesc);
		return fake.call(invocation, varargs);
	}

	private static boolean isRecording(StackTraceElement[] stackTrace) {
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (stackTraceElement.getClassName() != null && stackTraceElement.getClassName().startsWith("net.amygdalum.testrecorder.SnapshotManager")) {
				return true;
			}
		}
		return false;
	}

	public static FakeIO fake(Class<?> clazz) {
		return faked.computeIfAbsent(clazz.getName(), key -> new FakeIO(clazz));
	}

	private static FakeIOTransformer install() {
		Instrumentation inst = ByteBuddyAgent.install();
		installBridge(inst);
		return (FakeIOTransformer) new FakeIOTransformer().attach(inst);
	}

	private static void installBridge(Instrumentation inst) {
		try {
			inst.appendToBootstrapClassLoaderSearch(jarfile());
			BridgedFakeIO.callFake = MethodHandles.lookup().findStatic(FakeIO.class, "callFake",
				MethodType.methodType(Object.class, String.class, StackTraceElement[].class, Object.class, String.class, String.class, Object[].class));
			BridgedFakeIO.NO_RESULT = NO_RESULT;
		} catch (ReflectiveOperationException | IOException e) {
			throw new RuntimeException("failed installing fake bridge", e);
		}
	}

	private static JarFile jarfile() throws IOException {
		String bridge = "net/amygdalum/testrecorder/bridge/BridgedFakeIO.class";
		InputStream resourceStream = FakeIO.class.getResourceAsStream("/" + bridge);
		if (resourceStream == null) {
			throw new FileNotFoundException(bridge);
		}
		try (InputStream inputStream = resourceStream) {
			File agentJar = File.createTempFile("agent", "jar");
			agentJar.deleteOnExit();
			Manifest manifest = new Manifest();
			try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(agentJar), manifest)) {

				jarOutputStream.putNextEntry(new JarEntry(bridge));
				byte[] buffer = new byte[4096];
				int index;
				while ((index = inputStream.read(buffer)) != -1) {
					jarOutputStream.write(buffer, 0, index);
				}
				jarOutputStream.closeEntry();
			}
			return new JarFile(agentJar);
		}
	}

	public Input fakeInput(Aspect aspect) {
		Input input = new Input(this, aspect.getName(), aspect.getDesc());
		interactions.add(input);
		return input;
	}

	public boolean matches(Object instance, Class<?> staticClass) {
		return instance != null && clazz.isInstance(instance)
			|| staticClass == clazz;
	}

	public Output fakeOutput(Aspect aspect) {
		Output output = new Output(this, aspect.getName(), aspect.getDesc());
		interactions.add(output);
		return output;
	}

	public FakeIO setup() {
		Set<Class<?>> classes = resolveClasses();
		Set<String> methods = resolveMethods();

		fakeIOTransformer.addClasses(classes);
		fakeIOTransformer.addMethods(methods);
		fakeIOTransformer.restart(classes.toArray(new Class[0]));
		return this;
	}

	private Set<Class<?>> resolveClasses() {
		return interactions.stream().map(interaction -> interaction.resolve(clazz)).collect(toSet());
	}

	private Set<String> resolveMethods() {
		return interactions.stream().map(Interaction::getMethod).collect(toSet());
	}

	private Interaction findInteraction(Invocation invocation) {
		return interactions.stream()
			.filter(interaction -> interaction.matches(invocation))
			.findFirst()
			.orElse(null);
	}

	public Object call(Invocation invocation, Object... varargs) {
		Interaction interaction = findInteraction(invocation);
		if (interaction == null) {
			return NO_RESULT;
		}
		return interaction.call(invocation, varargs);
	}

	public void verify() {
		try {
			for (Interaction interaction : interactions) {
				interaction.verify();
			}
		} finally {
			interactions.clear();
			faked.remove(clazz.getName(), this);
			reset();
		}
	}

	public static void reset() {
		fakeIOTransformer.reset();
	}

	public static abstract class Interaction {

		protected FakeIO io;
		protected String methodName;
		protected String methodDesc;
		protected List<InvocationData> invocationData;

		public Interaction(FakeIO io, String methodName, String methodDesc) {
			this.io = io;
			this.methodName = methodName;
			this.methodDesc = methodDesc;
			this.invocationData = new ArrayList<>();
		}

		public Class<?> resolve(Class<?> clazz) {
			try {
				Method method = Types.getDeclaredMethod(clazz, methodName, argumentTypesFrom(methodDesc));
				return method.getDeclaringClass();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("failed to resolve class of virtual call", e);
			}
		}

		public boolean matches(Invocation invocation) {
			return io.matches(invocation.instance, invocation.clazz)
				&& methodName.equals(invocation.methodName)
				&& methodDesc.equals(invocation.methodDesc);
		}

		public Interaction add(Class<?> callerClass, String callerName, int callerLine, Object result, Object... args) {
			InvocationData data = new InvocationData(callerClass, callerName, callerLine, result, args);
			invocationData.add(data);
			return this;
		}

		public Object call(Invocation invocation, Object[] arguments) {
			Iterator<InvocationData> invocationDataIterator = invocationData.iterator();
			while (invocationDataIterator.hasNext()) {
				InvocationData next = invocationDataIterator.next();
				if (next.matchesCaller(invocation)) {
					Object result = call(next, arguments);
					invocationDataIterator.remove();
					return result;
				}
			}
			if (invocation.getCaller().startsWith("net.amygdalum.testrecorder.runtime")) {
				Object newValue = DefaultValue.INSTANCE.newValue(ByteCode.resultTypeFrom(methodDesc));
				return newValue;
			}
			throw new AssertionError("missing input for:\n" + invocation.getCallee() + " called from " + invocation.getCaller() + "\n\nIf the input was recorded ensure that all call sites are recorded");
		}

		public abstract Object call(InvocationData data, Object[] arguments);

		public String getMethod() {
			return methodName + methodDesc;
		}

		public Input fakeInput(Aspect aspect) {
			return io.fakeInput(aspect);
		}

		public Output fakeOutput(Aspect aspect) {
			return io.fakeOutput(aspect);
		}

		public FakeIO setup() {
			return io.setup();
		}

		protected String signatureFor(Object[] args) {
			return Arrays.stream(args)
				.map(arg -> arg instanceof Matcher<?> ? (Matcher<?>) arg : equalTo(arg))
				.map(matcher -> StringDescription.toString(matcher))
				.collect(joining(", ", methodName + "(", ")"));
		}

		public abstract void verify();

	}

	public static class Input extends Interaction {

		public Input(FakeIO io, String methodName, String methodDesc) {
			super(io, methodName, methodDesc);
		}

		@Override
		public Object call(InvocationData data, Object[] arguments) {
			sync(data.args, arguments);
			return data.result;
		}

		public void sync(Object[] fromArgs, Object[] toArgs) {
			for (int i = 0; i < toArgs.length; i++) {
				sync(fromArgs[i], toArgs[i]);
			}
		}

		public void sync(Object from, Object to) {
			Class<?> current = from.getClass();
			if (current.isArray()) {
				copyArrayValues(from, to);
				return;
			}
			for (Field field : allFields(current)) {
				copyField(field, from, to);
			}
		}

		public void verify() {
			if (!invocationData.isEmpty()) {
				StringBuilder msg = new StringBuilder("expected but not found:");
				for (InvocationData invocationDataItem : invocationData) {
					String expected = signatureFor(invocationDataItem.args);
					msg.append("\nexpected but not received call " + expected);
				}
				throw new AssertionError(msg);
			}
		}

	}

	public static class Output extends Interaction {

		public Output(FakeIO io, String methodName, String methodDesc) {
			super(io, methodName, methodDesc);
		}

		@Override
		public Object call(InvocationData data, Object[] arguments) {
			Object[] args = data.args;
			if (!verify(args, arguments)) {
				String expected = signatureFor(args);
				String found = signatureFor(arguments);
				throw new AssertionError("expected output:\n" + expected + "\nbut found:\n" + found);
			}
			return data.result;
		}

		private boolean verify(Object[] fromArgs, Object[] toArgs) {
			for (int i = 0; i < fromArgs.length; i++) {
				Object from = fromArgs[i];
				Object to = toArgs[i];
				if (from instanceof Matcher<?>) {
					if (!((Matcher<?>) from).matches(to)) {
						return false;
					}
				} else if (from == null) {
					if (to != null) {
						return false;
					}
				} else if (from != null) {
					if (from instanceof boolean[]) {
						if (!(to instanceof boolean[] && Arrays.equals((boolean[]) from, (boolean[]) to))) {
							return false;
						}
					} else if (from instanceof byte[]) {
						if (!(to instanceof byte[] && Arrays.equals((byte[]) from, (byte[]) to))) {
							return false;
						}
					} else if (from instanceof short[]) {
						if (!(to instanceof short[] && Arrays.equals((short[]) from, (short[]) to))) {
							return false;
						}
					} else if (from instanceof int[]) {
						if (!(to instanceof int[] && Arrays.equals((int[]) from, (int[]) to))) {
							return false;
						}
					} else if (from instanceof long[]) {
						if (!(to instanceof long[] && Arrays.equals((long[]) from, (long[]) to))) {
							return false;
						}
					} else if (from instanceof float[]) {
						if (!(to instanceof float[] && Arrays.equals((float[]) from, (float[]) to))) {
							return false;
						}
					} else if (from instanceof double[]) {
						if (!(to instanceof double[] && Arrays.equals((double[]) from, (double[]) to))) {
							return false;
						}
					} else if (from instanceof char[]) {
						if (!(to instanceof char[] && Arrays.equals((char[]) from, (char[]) to))) {
							return false;
						}
					} else if (from instanceof Object[]) {
						if (!(to instanceof Object[] && Arrays.equals((Object[]) from, (Object[]) to))) {
							return false;
						}
					} else if (!from.equals(to)) {
						return false;
					}
				}
			}
			return true;
		}

		public void verify() {
			if (!invocationData.isEmpty()) {
				StringBuilder msg = new StringBuilder("expected but not found:");
				for (InvocationData invocationDataItem : invocationData) {
					String expected = signatureFor(invocationDataItem.args);
					msg.append("\nexpected but not received call " + expected);
				}
				throw new AssertionError(msg);
			}
		}
	}

	protected static class InvocationData {
		public Class<?> callerClazz;
		public String callerName;
		private int callerLine;
		public Object result;
		public Object[] args;

		public InvocationData(Class<?> callerClazz, String callerName, int callerLine, Object result, Object[] args) {
			this.callerClazz = callerClazz;
			this.callerName = callerName;
			this.callerLine = callerLine;
			this.result = result;
			this.args = args;
		}

		public boolean matchesCaller(Invocation invocation) {
			return this.callerClazz.getName().equals(invocation.callerClassName)
				&& this.callerName.equals(invocation.callerMethodName)
				&& (this.callerLine == -1 || invocation.callerLine == -1 || this.callerLine == invocation.callerLine);
		}

	}

}
