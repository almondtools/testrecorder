package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.types.Computation.variable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.hints.Factory;
import net.amygdalum.testrecorder.hints.Name;
import net.amygdalum.testrecorder.runtime.DefaultValue;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class FactoryConstruction {

	private static final int NOT_MATCHED = -1;

	private DeserializerContext context;
	private SerializedObject serialized;
	private LocalVariable var;

	public FactoryConstruction(DeserializerContext context, LocalVariable var, SerializedObject value) {
		this.context = context;
		this.var = var;
		this.serialized = value;
	}

	public Computation build(TypeManager types, Deserializer generator) throws ReflectiveOperationException {
		Factory factory = context.getHint(serialized, Factory.class).orElseThrow(() -> new InstantiationException());
		Class<?> factoryClass = factory.clazz();
		String methodName = factory.method();

		FactoryModel model = assertFactoryConventions(factoryClass, methodName);

		return model.build(types, generator);
	}

	private FactoryModel assertFactoryConventions(Class<?> factoryClass, String methodName) throws ReflectiveOperationException {
		List<SerializedValue> parameters = Arrays.stream(factoryClass.getDeclaredMethods())
			.filter(method -> method.getName().equals(methodName))
			.map(method -> assignParametersFor(method))
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(List::size))
			.findFirst()
			.orElseThrow(() -> new NoSuchMethodException());

		return new FactoryModel(var, factoryClass, methodName, parameters);
	}

	private List<SerializedValue> assignParametersFor(Method method) {
		Parameter[] parameters = method.getParameters();
		SerializedValue[] values = new SerializedValue[parameters.length];
		for (SerializedField field : serialized.getFields()) {
			Class<?> fieldClass = Types.baseType(field.getType());
			String fieldName = field.getName();
			SerializedValue value = field.getValue();

			int bestCandidate = matchBestCandidate(parameters, fieldClass, fieldName);
			if (bestCandidate == NOT_MATCHED) {
				if (canBeOmitted(field.getType(), value)) {
					continue;
				}
				return null;
			} else if (values[bestCandidate] != null) {
				return null;
			}
			values[bestCandidate] = value;
		}

		List<SerializedValue> arguments = asList(values);
		if (arguments.contains(null)) {
			return null;
		}
		return arguments;
	}

	private boolean canBeOmitted(Type type, SerializedValue value) {
		if (value instanceof SerializedNull) {
			return true;
		}
		if (value instanceof SerializedLiteral) {
			SerializedLiteral literal = (SerializedLiteral) value;
			if (literal.getValue() != null && literal.getValue().equals(DefaultValue.of(type))) {
				return true;
			}
		}
		return false;
	}

	private int matchBestCandidate(Parameter[] parameters, Class<?> fieldClass, String fieldName) {
		int bestCandidate = NOT_MATCHED;
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].getType() == fieldClass) {
				if (bestCandidate == NOT_MATCHED) {
					bestCandidate = i;
				} else {
					Name name = parameters[i].getAnnotation(Name.class);
					if (name != null && name.value().equals(fieldName)) {
						bestCandidate = i;
					}
				}
			}
		}
		return bestCandidate;
	}

	private static class FactoryModel {

		private String name;
		private Type type;
		private Class<?> factoryClass;
		private String methodName;
		private List<SerializedValue> arguments;

		FactoryModel(LocalVariable var, Class<?> factoryClass, String methodName, List<SerializedValue> arguments) {
			this.name = var.getName();
			this.type = var.getType();
			this.factoryClass = factoryClass;
			this.methodName = methodName;
			this.arguments = arguments;
		}

		public Computation build(TypeManager types, Deserializer generator) {
			types.registerTypes(factoryClass);

			List<String> statements = new ArrayList<>();

			List<String> computedArguments = new ArrayList<String>();
			for (SerializedValue argument : arguments) {
				Computation argumentComputation = argument.accept(generator);
				statements.addAll(argumentComputation.getStatements());
				computedArguments.add(argumentComputation.getValue());
			}

			String result = callMethod(types.getVariableTypeName(factoryClass), methodName, computedArguments);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, result));
			return variable(name, type, statements);
		}

	}

}
