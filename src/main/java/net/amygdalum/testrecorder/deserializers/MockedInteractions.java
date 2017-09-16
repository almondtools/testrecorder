package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodChainExpression;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodChainStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.classOf;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.runtime.InputDecorator;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;
import net.bytebuddy.implementation.bind.ArgumentTypeResolver;

public class MockedInteractions {

	public static final MockedInteractions NONE = new MockedInteractions(emptyList(), emptyList());

	private List<SerializedInput> setupInput;
	private List<SerializedOutput> expectOutput;

	public MockedInteractions(List<SerializedInput> setupInput, List<SerializedOutput> expectOutput) {
		this.setupInput = notNull(setupInput);
		this.expectOutput = notNull(expectOutput);
	}

	private static <T> List<T> notNull(List<T> list) {
		if (list == null) {
			return emptyList();
		}
		return list;
	}

	public boolean hasInputInteractions(SerializedReferenceType value) {
		return setupInput.stream()
			.anyMatch(input -> input.getId() == value.getId());
	}

	public Computation generateInputInteractions(SerializedReferenceType value, Computation computation, LocalVariableNameGenerator locals, TypeManager types, SetupGenerators setup) {
		List<String> statements = new ArrayList<>(computation.getStatements());
		types.registerImport(InputDecorator.class);
		String val = newObject(types.getConstructorTypeName(InputDecorator.class), computation.getValue());

		List<String> methods = new ArrayList<>();
		for (SerializedInput in : setupInput.stream()
			.filter(input -> input.getId() == value.getId())
			.collect(toList())) {

			Class<?> declaringClass = deanonymized(in.getDeclaringClass());
			types.registerImport(declaringClass);

			Computation result = null;
			if (in.getResult() != null) {
				result = in.getResult().accept(setup);
				statements.addAll(result.getStatements());
			}

			List<Computation> args = Stream.of(in.getValues())
				.map(arg -> arg.accept(setup))
				.collect(toList());

			statements.addAll(args.stream()
				.flatMap(arg -> arg.getStatements().stream())
				.collect(toList()));

			List<String> arguments = new ArrayList<>();

			arguments.add(asLiteral(in.getName()));

			if (in.getTypes().length > 0) {
				List<String> argTypes = Arrays.stream(in.getTypes())
					.map(type -> types.getRawClass(type))
					.collect(toList());
				arguments.add(arrayLiteral(types.getConstructorTypeName(Class[].class), argTypes));
			}

			if (result != null) {
				arguments.add(result.getValue());
			} else {
				arguments.add("null");
			}

			arguments.addAll(args.stream()
				.map(arg -> arg.getValue())
				.collect(toList()));

			methods.add(callLocalMethod("provide", arguments));

		}
		methods.add(callLocalMethod("setup"));

		val = callMethodChainExpression(val, methods);

		String var = locals.fetchName(computation.getType());
		statements.add(assignLocalVariableStatement(types.getVariableTypeName(computation.getType()), var, val));
		return new Computation(var, computation.getType(), true, statements);
	}

	public boolean hasOutputInteractions(SerializedReferenceType value) {
		return setupInput.stream()
			.anyMatch(input -> input.getId() == value.getId());
	}

	public Computation generateOutputInteractions(SerializedReferenceType value, Computation output, LocalVariableNameGenerator locals, TypeManager types, MatcherGenerators matcher) {
		return output;
	}

	private Class<?> deanonymized(Class<?> declaringClass) {
		while (declaringClass.isAnonymousClass()) {
			declaringClass = declaringClass.getSuperclass();
		}
		return declaringClass;
	}

}
