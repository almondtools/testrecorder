package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodChainExpression;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.core.CombinableMatcher;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.runtime.InputDecorator;
import net.amygdalum.testrecorder.runtime.OutputDecorator;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class MockedInteractions {

	public static final MockedInteractions NONE = new MockedInteractions(null, null, emptyList(), emptyList());

	private DeserializerFactory setupFactory;
	private DeserializerFactory matcherFactory;

	private List<SerializedInput> setupInput;
	private List<SerializedOutput> expectOutput;

	public MockedInteractions(DeserializerFactory setup, DeserializerFactory matcher, List<SerializedInput> setupInput, List<SerializedOutput> expectOutput) {
		this.setupFactory = setup;
		this.matcherFactory = matcher;
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
		return setupFactory != null
			&& matcherFactory != null
			&& setupInput.stream()
				.anyMatch(input -> input.getId() == value.getId());
	}

	public Computation prepareInputInteractions(SerializedReferenceType value, Computation computation, LocalVariableNameGenerator locals, TypeManager types) {
		Deserializer<Computation> setup = setupFactory.create(locals, types);

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

		if (computation.isStored()) {
			String var = computation.getValue();
			statements.add(assignLocalVariableStatement(var, val));
			return new Computation(var, computation.getType(), true, statements);
		} else {
			String var = locals.fetchName(computation.getType());
			statements.add(assignLocalVariableStatement(types.getVariableTypeName(computation.getType()), var, val));
			return new Computation(var, computation.getType(), true, statements);
		}
	}

	public Computation verifyInputInteractions(SerializedReferenceType value, Computation computation, LocalVariableNameGenerator locals, TypeManager types) {
		return computation;
	}

	public boolean hasOutputInteractions(SerializedReferenceType value) {
		return setupFactory != null
			&& matcherFactory != null
			&& expectOutput.stream()
				.anyMatch(output -> output.getId() == value.getId());
	}

	public Computation prepareOutputInteractions(SerializedReferenceType value, Computation computation, LocalVariableNameGenerator locals, TypeManager types) {
		Deserializer<Computation> setup = setupFactory.create(locals, types);
		Deserializer<Computation> matcher = matcherFactory.create(locals, types);
		List<String> statements = new ArrayList<>(computation.getStatements());
		types.registerImport(OutputDecorator.class);
		String val = newObject(types.getConstructorTypeName(OutputDecorator.class), computation.getValue());

		List<String> methods = new ArrayList<>();
		for (SerializedOutput out : expectOutput.stream()
			.filter(output -> output.getId() == value.getId())
			.collect(toList())) {
			types.registerImport(out.getDeclaringClass());

			Computation result = null;
			if (out.getResult() != null) {
				result = out.getResult().accept(setup);
				statements.addAll(result.getStatements());
			}

			List<Computation> args = Stream.of(out.getValues())
				.map(arg -> arg.accept(matcher))
				.collect(toList());

			statements.addAll(args.stream()
				.flatMap(arg -> arg.getStatements().stream())
				.collect(toList()));

			List<String> arguments = new ArrayList<>();

			arguments.add(asLiteral(out.getName()));

			List<String> argTypes = Arrays.stream(out.getTypes())
				.map(type -> types.getRawClass(type))
				.collect(toList());
			arguments.add(arrayLiteral(types.getConstructorTypeName(Class[].class), argTypes));

			if (result != null) {
				arguments.add(result.getValue());
			} else {
				arguments.add("null");
			}

			arguments.addAll(args.stream()
				.map(arg -> arg.getValue())
				.collect(toList()));

			methods.add(callLocalMethod("expect", arguments));
		}
		methods.add(callLocalMethod("end"));

		val = callMethodChainExpression(val, methods);

		if (computation.isStored()) {
			String var = computation.getValue();
			statements.add(assignLocalVariableStatement(var, val));
			return new Computation(var, computation.getType(), true, statements);
		} else {
			String var = locals.fetchName(computation.getType());
			statements.add(assignLocalVariableStatement(types.getVariableTypeName(computation.getType()), var, val));
			return new Computation(var, computation.getType(), true, statements);
		}
	}

	public Computation verifyOutputInteractions(SerializedReferenceType value, Computation computation, LocalVariableNameGenerator locals, TypeManager types) {
		types.registerImport(OutputDecorator.class);
		types.registerImport(CombinableMatcher.class);

		String base = types.getRawTypeName(CombinableMatcher.class);

		base = callMethod(base, "both", computation.getValue());

		String val = callMethod(base, "and", callMethod(types.getRawTypeName(OutputDecorator.class), "verifies"));

		return new Computation(val, computation.getType(), computation.isStored(), computation.getStatements());
	}

	private Class<?> deanonymized(Class<?> declaringClass) {
		while (declaringClass.isAnonymousClass()) {
			declaringClass = declaringClass.getSuperclass();
		}
		return declaringClass;
	}

}
