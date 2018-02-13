package net.amygdalum.testrecorder;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Computation.variable;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodChainExpression;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.methodDeclaration;
import static net.amygdalum.testrecorder.deserializers.Templates.newAnonymousClassInstance;
import static net.amygdalum.testrecorder.deserializers.Templates.returnStatement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.Templates;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.runtime.Aspect;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedInteraction;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class MockedInteractions {

	public static final MockedInteractions NONE = new MockedInteractions(null, null, emptyList(), emptyList());

	private DeserializerFactory setupFactory;
	private DeserializerFactory matcherFactory;

	private Collection<SerializedInput> input;
	private Collection<SerializedOutput> output;

	private List<String> fakeClassVariables;

	public MockedInteractions(DeserializerFactory setup, DeserializerFactory matcher, Collection<SerializedInput> setupInput, Collection<SerializedOutput> expectOutput) {
		this.setupFactory = setup;
		this.matcherFactory = matcher;
		this.input = setupInput;
		this.output = expectOutput;

		this.fakeClassVariables = new ArrayList<>();
	}

	public List<String> prepare(LocalVariableNameGenerator locals, TypeManager types, DeserializerContext context) {
		if (setupFactory == null || matcherFactory == null) {
			return emptyList();
		} else if (input.isEmpty() && output.isEmpty()) {
			return emptyList();
		}
		Deserializer<Computation> setup = setupFactory.create(locals, types);
		Deserializer<Computation> matcher = matcherFactory.create(locals, types);

		types.registerTypes(FakeIO.class, Aspect.class);

		Map<Class<?>, List<SerializedInteraction>> ioByClass = Stream.concat(input.stream(), output.stream())
			.collect(groupingBy(SerializedInteraction::getDeclaringClass));

		List<String> statements = new ArrayList<>();

		String fakeIOType = types.getRawTypeName(FakeIO.class);
		for (Map.Entry<Class<?>, List<SerializedInteraction>> entry : ioByClass.entrySet()) {
			Class<?> clazz = entry.getKey();
			List<SerializedInteraction> interactions = entry.getValue();
			String val = callMethod(fakeIOType, "fake", types.getRawClass(clazz));

			Map<String, List<SerializedInteraction>> interactionsByAspect = new LinkedHashMap<>();
			for (SerializedInteraction interaction : interactions) {
				LocalVariableNameGenerator methodParams = new LocalVariableNameGenerator();
				String dummyBody = interaction.getResultType() == void.class
					? ""
					: returnStatement(nullValue(interaction.getResultType()));
				String method = methodDeclaration("public",
					types.getRawTypeName(interaction.getResultType()),
					interaction.getName(),
					Arrays.stream(interaction.getTypes())
						.map(type -> Templates.param(types.getRawTypeName(type), methodParams.fetchName(type)))
						.collect(toList()),
					dummyBody);

				String aspect = newAnonymousClassInstance(types.getRawTypeName(Aspect.class), emptyList(), method);

				String fake = callLocalMethod(fakeMethod(interaction), aspect);

				List<SerializedInteraction> aspectInteractions = interactionsByAspect.computeIfAbsent(fake, key -> new ArrayList<>());
				aspectInteractions.add(interaction);
			}

			List<String> methods = new ArrayList<>();
			for (Map.Entry<String, List<SerializedInteraction>> aspectInteractions : interactionsByAspect.entrySet()) {
				String aspect = aspectInteractions.getKey();
				methods.add(aspect);
				for (SerializedInteraction interaction : aspectInteractions.getValue()) {
					Deserializer<Computation> deserializer = deserializerFor(interaction, setup, matcher);			
					List<String> arguments = new ArrayList<>();

					if (interaction.hasResult()) {
						Computation resultComputation = interaction.getResult().accept(setup, context);
						statements.addAll(resultComputation.getStatements());
						arguments.add(resultComputation.getValue());
					} else {
						Computation resultComputation = variable("null", interaction.getResultType());
						arguments.add(resultComputation.getValue());
					}

					List<Computation> argumentsComputation = Arrays.stream(interaction.getArguments())
						.map(argument -> argument.accept(deserializer, context))
						.collect(toList());
					argumentsComputation.forEach(argumentComputation -> {
						statements.addAll(argumentComputation.getStatements());
						arguments.add(argumentComputation.getValue());
					});

					//TODO replace it by addStatic/addVirtual
					methods.add(callLocalMethod("add", arguments));
				}
			}
			val = callMethodChainExpression(val, methods);

			String fakeClassVariable = locals.fetchName(clazz);
			fakeClassVariables.add(fakeClassVariable);
			statements.add(assignLocalVariableStatement(fakeIOType, fakeClassVariable, callMethod(val, "setup")));
		}

		return statements;
	}

	private Deserializer<Computation> deserializerFor(SerializedInteraction interaction, Deserializer<Computation> setup, Deserializer<Computation> matcher) {
		if (interaction instanceof SerializedInput) {
			return setup;
		} else if (interaction instanceof SerializedOutput) {
			return matcher;
		} else {
			throw new DeserializationException("unknown faking: " + interaction.getClass());
		}
	}

	private String fakeMethod(SerializedInteraction interaction) {
		if (interaction instanceof SerializedInput) {
			return "fakeInput";
		} else if (interaction instanceof SerializedOutput) {
			return "fakeOutput";
		} else {
			throw new DeserializationException("unknown faking: " + interaction.getClass());
		}
	}

	private String nullValue(Type type) {
		if (!Types.isPrimitive(type)) {
			return "null";
		} else if (type == boolean.class) {
			return "false";
		} else if (type == char.class) {
			return "(char) 0";
		} else if (type == byte.class || type == short.class || type == int.class || type == long.class) {
			return "0";
		} else if (type == float.class || type == double.class) {
			return "0.0f";
		} else {
			return "null";
		}
	}

	public List<String> verify(LocalVariableNameGenerator locals, TypeManager types, DeserializerContext context) {
		List<String> statements = new ArrayList<>();
		for (String fakeClassVariable : fakeClassVariables) {
			statements.add(callMethodStatement(fakeClassVariable, "verify"));
		}
		return statements;
	}

}
