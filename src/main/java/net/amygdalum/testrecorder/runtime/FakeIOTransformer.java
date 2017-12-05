package net.amygdalum.testrecorder.runtime;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.asm.ByteCode.isNative;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.asm.GetClassName;
import net.amygdalum.testrecorder.asm.GetMethodDesc;
import net.amygdalum.testrecorder.asm.GetMethodName;
import net.amygdalum.testrecorder.asm.GetStackTrace;
import net.amygdalum.testrecorder.asm.GetThisOrNull;
import net.amygdalum.testrecorder.asm.InvokeStatic;
import net.amygdalum.testrecorder.asm.MethodContext;
import net.amygdalum.testrecorder.asm.ReturnDummy;
import net.amygdalum.testrecorder.asm.ReturnFakeOrProceed;
import net.amygdalum.testrecorder.asm.Sequence;
import net.amygdalum.testrecorder.asm.SequenceInstruction;
import net.amygdalum.testrecorder.asm.WrapArguments;
import net.amygdalum.testrecorder.bridge.BridgedFakeIO;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;

public class FakeIOTransformer extends AttachableClassFileTransformer implements ClassFileTransformer {

	private Instrumentation inst;

	private Set<Class<?>> classes;
	private Set<String> methods;

	private Set<Class<?>> nativeClasses;

	public FakeIOTransformer() {
		this.classes = new LinkedHashSet<>();
		this.methods = new LinkedHashSet<>();
		this.nativeClasses = new LinkedHashSet<>();
	}

	@Override
	public FakeIOTransformer attach(Instrumentation inst) {
		this.inst = inst;
		FakeIOTransformer attach = (FakeIOTransformer) super.attach(inst);
		return attach;
	}

	public void addClasses(Collection<Class<?>> classes) {
		this.classes.addAll(classes);
	}

	public void addMethods(Collection<String> methods) {
		this.methods.addAll(methods);
	}

	public void restart(Class<?>... classes) {
		try {
			if (classes != null && classes.length != 0) {
				inst.retransformClasses(classes);
			}
		} catch (UnmodifiableClassException e) {
			System.err.println("unexpected class transforming restriction: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	public Class<?>[] classesToRetransform() {
		return classes.toArray(new Class[0]);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			if (className != null && matches(className)) {
				ClassReader cr = new ClassReader(classfileBuffer);
				ClassNode classNode = new ClassNode();

				cr.accept(classNode, 0);
				insertIOFakes(classNode, classBeingRedefined);

				ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				classNode.accept(out);
				return out.toByteArray();
			}
			return null;
		} catch (Throwable e) {
			System.err.println("transformation error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	private boolean matches(String className) {
		for (Class<?> clazz : classes) {
			if (Type.getInternalName(clazz).equals(className)) {
				return true;
			}
		}
		return false;
	}

	private boolean needsBridging(ClassNode classNode, Class<?> clazz) {
		if (clazz != null && clazz.getClassLoader() == null) {
			return true;
		}
		return false;
	}

	private void insertIOFakes(ClassNode classNode, Class<?> clazz) {
		for (MethodNode methodNode : fakedMethods(classNode)) {
			insertIOFakes(classNode, methodNode, clazz);
		}
	}

	private void insertIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		InsnList insnList = createIOFakes(classNode, methodNode, clazz).build(new MethodContext(classNode, methodNode));
		methodNode.instructions.insert(insnList);
	}

	private Sequence createIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		if (isNative(methodNode)) {
			return createNativeIOFakes(classNode, methodNode, clazz);
		} else {
			return createOrdinaryIOFakes(classNode, methodNode, clazz);
		}
	}

	private Sequence createNativeIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		nativeClasses.add(clazz);
		methodNode.access = methodNode.access & ~Opcodes.ACC_NATIVE;

		return Sequence.start()
			.then(createCommonIOFakes(classNode, methodNode, clazz))
			.then(new ReturnDummy());
	}

	private Sequence createOrdinaryIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		return Sequence.start()
			.then(createCommonIOFakes(classNode, methodNode, clazz));
	}

	private SequenceInstruction createCommonIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		if (needsBridging(classNode, clazz)) {
			return createBridgedIOFake(classNode, methodNode);
		} else {
			return createDirectIOFake(classNode, methodNode);
		}
	}

	protected SequenceInstruction createBridgedIOFake(ClassNode classNode, MethodNode methodNode) {
		return Sequence.start()
			.then(new InvokeStatic(BridgedFakeIO.class, "callFake", String.class, StackTraceElement[].class, Object.class, String.class, String.class, Object[].class)
				.withArgument(0, new GetClassName())
				.withArgument(1, new GetStackTrace())
				.withArgument(2, new GetThisOrNull())
				.withArgument(3, new GetMethodName())
				.withArgument(4, new GetMethodDesc())
				.withArgument(5, new WrapArguments()))
			.then(new ReturnFakeOrProceed(BridgedFakeIO.class, "NO_RESULT"));
	}

	protected SequenceInstruction createDirectIOFake(ClassNode classNode, MethodNode methodNode) {
		return Sequence.start()
			.then(new InvokeStatic(FakeIO.class, "callFake", String.class, StackTraceElement[].class, Object.class, String.class, String.class, Object[].class)
				.withArgument(0, new GetClassName())
				.withArgument(1, new GetStackTrace())
				.withArgument(2, new GetThisOrNull())
				.withArgument(3, new GetMethodName())
				.withArgument(4, new GetMethodDesc())
				.withArgument(5, new WrapArguments()))
			.then(new ReturnFakeOrProceed(FakeIO.class, "NO_RESULT"));
	}

	private List<MethodNode> fakedMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> methods.contains(method.name + method.desc))
			.collect(toList());
	}

	public void reset() {
		Class<?>[] classesToReset = classes.toArray(new Class[0]);
		Class<?>[] classesToRelink = nativeClasses.toArray(new Class[0]);
		classes.clear();
		methods.clear();
		nativeClasses.clear();
		if (classesToReset.length > 0) {
			try {
				inst.retransformClasses(classesToReset);
			} catch (UnmodifiableClassException e) {
				System.err.println("unexpected class transforming restriction: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		for (Class<?> clazz : classesToRelink) {
			relinkNativeMethods(clazz);
		}
	}

	private static void relinkNativeMethods(Class<?> clazz) {
		Method registerNatives = findRegisterNatives(clazz);

		if (registerNatives != null) {
			try {
				registerNatives.setAccessible(true);
				registerNatives.invoke(null);
			} catch (ReflectiveOperationException e) {
			}
		}
	}

	private static Method findRegisterNatives(Class<?> clazz) {
		try {
			return clazz.getDeclaredMethod("registerNatives");
		} catch (NoSuchMethodException e) {
		}
		try {
			return clazz.getDeclaredMethod("initIDs");
		} catch (NoSuchMethodException e) {
		}
		return null;
	}

}