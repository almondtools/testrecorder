package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodChainExpression;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldDeclaration;
import static net.amygdalum.testrecorder.deserializers.Templates.methodDeclaration;
import static net.amygdalum.testrecorder.deserializers.Templates.newAnonymousClassInstance;
import static net.amygdalum.testrecorder.deserializers.Templates.newArray;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.deserializers.Templates.param;
import static net.amygdalum.testrecorder.deserializers.Templates.returnStatement;
import static net.amygdalum.testrecorder.util.Types.boxedType;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mockit.Invocation;
import mockit.Mock;
import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.runtime.FakeCalls;
import net.amygdalum.testrecorder.runtime.FakeClass;
import net.amygdalum.testrecorder.runtime.FakeIn;
import net.amygdalum.testrecorder.runtime.FakeOut;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class MockedInteractions {

	public static final MockedInteractions NONE = new MockedInteractions(null, null, emptyList(), emptyList());

	private DeserializerFactory setupFactory;
	private DeserializerFactory matcherFactory;

	private List<SerializedInput> setupInput;
	private List<SerializedOutput> expectOutput;
	
	private Set<String> fakeClassVariables;

	public MockedInteractions(DeserializerFactory setup, DeserializerFactory matcher, List<SerializedInput> setupInput, List<SerializedOutput> expectOutput) {
		this.setupFactory = setup;
		this.matcherFactory = matcher;
		this.setupInput = setupInput;
		this.expectOutput = expectOutput;
		
		this.fakeClassVariables = new HashSet<>();
	}

	public List<String> prepareInput(List<SerializedInput> input, LocalVariableNameGenerator locals, TypeManager types, DeserializerContext context) {
		if (setupFactory == null || matcherFactory == null || setupInput.isEmpty()) {
			return emptyList();
		}
		types.registerTypes(FakeClass.class, FakeCalls.class, FakeIn.class, Mock.class, Invocation.class);

		Map<Class<?>, List<SerializedInput>> inputByClass = input.stream()
			.collect(Collectors.groupingBy(SerializedInput::getDeclaringClass));

		List<String> statements = new ArrayList<>();
		for (Map.Entry<Class<?>, List<SerializedInput>> entry : inputByClass.entrySet()) {
			Class<?> clazz = entry.getKey();
			List<SerializedInput> inputs = entry.getValue();
			String body = new FakeClassBody(clazz, statements, locals, types, context)
				.addInputs(inputs)
				.build();
			String type = types.getVariableTypeName(Types.parameterized(FakeClass.class, null, clazz));
			String val = newAnonymousClassInstance(type, emptyList(), body);
			String faked = locals.fetchName("faked");
			fakeClassVariables.add(faked);
			statements.add(assignLocalVariableStatement(type, faked, val));
		}

		return statements;
	}

	public List<String> prepareOutput(List<SerializedOutput> output, LocalVariableNameGenerator locals, TypeManager types, DeserializerContext context) {
		if (setupFactory == null || matcherFactory == null || expectOutput.isEmpty()) {
			return emptyList();
		}
		types.registerTypes(FakeClass.class, FakeCalls.class, FakeOut.class, Mock.class, Invocation.class);

		Map<Class<?>, List<SerializedOutput>> outputByClass = output.stream()
			.collect(Collectors.groupingBy(SerializedOutput::getDeclaringClass));

		List<String> statements = new ArrayList<>();
		for (Map.Entry<Class<?>, List<SerializedOutput>> entry : outputByClass.entrySet()) {
			Class<?> clazz = entry.getKey();
			List<SerializedOutput> outputs = entry.getValue();
			String body = new FakeClassBody(clazz, statements, locals, types, context)
				.addOutputs(outputs)
				.build();
			String type = types.getVariableTypeName(Types.parameterized(FakeClass.class, null, clazz));
			String val = newAnonymousClassInstance(type, emptyList(), body);
			String faked = locals.fetchName("faked");
			fakeClassVariables.add(faked);
			statements.add(assignLocalVariableStatement(type, faked, val));
		}

		return statements;
	}

	public List<String> verify(LocalVariableNameGenerator locals, TypeManager types, DeserializerContext context) {
		List<String> statements = new ArrayList<>();
		for (String fakeClassVar : fakeClassVariables) {
			statements.add(Templates.callMethodStatement(fakeClassVar, "verify"));
		}
		return statements;
	}

	private class FakeClassBody {

		private Class<?> clazz;
		private Deserializer<Computation> setup;
		private Deserializer<Computation> matcher;
		private List<String> statements;
		private LocalVariableNameGenerator locals;
		private TypeManager types;
		private DeserializerContext context;

		private Map<Method, List<SerializedInput>> inmethods;
		private Map<Method, List<SerializedOutput>> outmethods;

		public FakeClassBody(Class<?> clazz, List<String> statements, LocalVariableNameGenerator locals, TypeManager types, DeserializerContext context) {
			this.clazz = clazz;
			this.statements = statements;
			this.locals = locals;
			this.types = types;
			this.context = context;
			this.inmethods = new HashMap<>();
			this.outmethods = new HashMap<>();
			this.setup = setupFactory.create(locals, types);
			this.matcher = matcherFactory.create(locals, types);
		}

		public FakeClassBody addInputs(List<SerializedInput> inputs) {
			for (SerializedInput input : inputs) {
				addInput(input);
			}
			return this;
		}

		private void addInput(SerializedInput input) {
			Method method = resolveMethod(input.getName(), input.getTypes());
			inmethods.computeIfAbsent(method, key -> new ArrayList<>()).add(input);
		}

		public FakeClassBody addOutputs(List<SerializedOutput> outputs) {
			for (SerializedOutput output : outputs) {
				addOutput(output);
			}
			return this;
		}

		private void addOutput(SerializedOutput output) {
			Method method = resolveMethod(output.getName(), output.getTypes());
			outmethods.computeIfAbsent(method, key -> new ArrayList<>()).add(output);
		}

		private Method resolveMethod(String name, Type[] types) {
			try {
				Class<?>[] parameterTypes = Arrays.stream(types).map(Types::baseType).toArray(Class[]::new);
				return Types.getDeclaredMethod(clazz, name, parameterTypes);
			} catch (NoSuchMethodException e) {
				return null;
			}
		}

		public String build() {
			StringBuilder buffer = new StringBuilder("\n");
			for (Map.Entry<Method, List<SerializedInput>> methodEntry : inmethods.entrySet()) {
				Method method = methodEntry.getKey();
				List<SerializedInput> methodInput = methodEntry.getValue();

				String var = locals.fetchName(method.getName());
				String inputProvider = provideInput(method, methodInput, var);

				buffer
					.append("\n")
					.append(inputProvider)
					.append("\n");

				String inputDelegator = callInput(method, var);

				buffer
					.append("\n")
					.append(inputDelegator)
					.append("\n");
			}
			for (Map.Entry<Method, List<SerializedOutput>> methodEntry : outmethods.entrySet()) {
				Method method = methodEntry.getKey();
				List<SerializedOutput> methodOutput = methodEntry.getValue();

				String var = locals.fetchName(method.getName());
				String outputConsumer = provideOutput(method, methodOutput, var);

				buffer
					.append("\n")
					.append(outputConsumer)
					.append("\n");

				String outputDelegator = callOutput(method, var);

				buffer
					.append("\n")
					.append(outputDelegator)
					.append("\n");
			}

			return buffer.toString();
		}

		private String provideInput(Method method, List<SerializedInput> methodInput, String var) {
			Class<?> returnType = boxedType(method.getReturnType());
			String fieldType = types.getVariableTypeName(parameterized(FakeCalls.class, null, returnType));
			String constructorType = types.getConstructorTypeName(parameterized(FakeIn.class, null, returnType));
			String base = types.getRawClass(clazz);
			String methodName = asLiteral(method.getName());
			String paramTypes = method.getParameterTypes().length == 0
				? newArray(types.getRawTypeName(Class.class), "0")
				: arrayLiteral(types.getRawTypeName(Class[].class), Arrays.stream(method.getParameterTypes())
					.map(paramType -> types.getRawClass(paramType))
					.collect(toList()));
			String value = newObject(constructorType, base, methodName, paramTypes);
			List<String> methods = new ArrayList<>();
			for (SerializedInput in : methodInput) {
				Computation result = null;
				if (in.getResult() != null) {
					result = in.getResult().accept(setup, context);
					statements.addAll(result.getStatements());
				}

				List<Computation> args = Stream.of(in.getArguments())
					.map(arg -> arg.accept(setup, context))
					.collect(toList());

				statements.addAll(args.stream()
					.flatMap(arg -> arg.getStatements().stream())
					.collect(toList()));

				List<String> arguments = new ArrayList<>();
				arguments.add(asLiteral(in.getCaller()));
				if (result != null) {
					arguments.add(result.getValue());
				} else {
					arguments.add("null");
				}

				arguments.addAll(args.stream()
					.map(arg -> arg.getValue())
					.collect(toList()));

				methods.add(callLocalMethod("add", arguments));
			}
			value = callMethodChainExpression(value, methods);

			return fieldDeclaration(null, fieldType, var, value);
		}

		private String callInput(Method method, String var) {
			String returnTypeName = types.getRawTypeName(method.getReturnType());
			String modifiers = Templates.annotation(types.getRawTypeName(Mock.class));
			LocalVariableNameGenerator methodLocals = new LocalVariableNameGenerator();
			List<String> paramTypeNames = new ArrayList<>();
			String invocation = methodLocals.fetchName(Invocation.class);
			paramTypeNames.add(param(types.getRawTypeName(Invocation.class), invocation));
			Arrays.stream(method.getParameterTypes())
				.map(type -> param(types.getRawTypeName(type), methodLocals.fetchName(type)))
				.forEach(paramTypeNames::add);
			String body = method.getReturnType() == void.class
				? callMethodStatement(var, "next", invocation)
				: returnStatement(callMethod(var, "next", invocation));
			return methodDeclaration(modifiers, returnTypeName, method.getName(), paramTypeNames, body);
		}

		private String provideOutput(Method method, List<SerializedOutput> methodOutput, String var) {
			Class<?> returnType = boxedType(method.getReturnType());
			String fieldType = types.getVariableTypeName(parameterized(FakeCalls.class, null, returnType));
			String constructorType = types.getConstructorTypeName(parameterized(FakeOut.class, null, returnType));
			String base = types.getRawClass(clazz);
			String methodName = asLiteral(method.getName());
			String paramTypes = method.getParameterTypes().length == 0
				? newArray(types.getRawTypeName(Class.class), "0")
				: arrayLiteral(types.getRawTypeName(Class[].class), Arrays.stream(method.getParameterTypes())
					.map(paramType -> types.getRawClass(paramType))
					.collect(toList()));
			String value = newObject(constructorType, base, methodName, paramTypes);
			List<String> methods = new ArrayList<>();
			for (SerializedOutput out : methodOutput) {
				Computation result = null;
				if (out.getResult() != null) {
					result = out.getResult().accept(setup, context);
					statements.addAll(result.getStatements());
				}

				List<Computation> args = Stream.of(out.getArguments())
					.map(arg -> arg.accept(matcher, context))
					.collect(toList());

				statements.addAll(args.stream()
					.flatMap(arg -> arg.getStatements().stream())
					.collect(toList()));

				List<String> arguments = new ArrayList<>();
				arguments.add(asLiteral(out.getCaller()));
				if (result != null) {
					arguments.add(result.getValue());
				} else {
					arguments.add("null");
				}

				arguments.addAll(args.stream()
					.map(arg -> arg.getValue())
					.collect(toList()));

				methods.add(callLocalMethod("add", arguments));
			}
			value = callMethodChainExpression(value, methods);

			return fieldDeclaration(null, fieldType, var, value);
		}

		private String callOutput(Method method, String var) {
			String returnTypeName = types.getRawTypeName(method.getReturnType());
			String modifiers = Templates.annotation(types.getRawTypeName(Mock.class));
			LocalVariableNameGenerator methodLocals = new LocalVariableNameGenerator();
			List<String> paramTypeNames = new ArrayList<>();
			String invocation = methodLocals.fetchName(Invocation.class);
			paramTypeNames.add(param(types.getRawTypeName(Invocation.class), invocation));
			Arrays.stream(method.getParameterTypes())
				.map(type -> param(types.getRawTypeName(type), methodLocals.fetchName(type)))
				.forEach(paramTypeNames::add);
			String body = method.getReturnType() == void.class
				? callMethodStatement(var, "next", invocation)
				: returnStatement(callMethod(var, "next", invocation));
			return methodDeclaration(modifiers, returnTypeName, method.getName(), paramTypeNames, body);
		}

	}

}
