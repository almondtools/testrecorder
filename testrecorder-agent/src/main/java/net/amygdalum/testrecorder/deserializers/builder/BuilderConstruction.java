package net.amygdalum.testrecorder.deserializers.builder;

import static java.lang.Character.toUpperCase;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.getDeclaredMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.hints.Builder;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedObject;

public class BuilderConstruction {

	private static final String WITH = "with";
	private static final String BUILD = "build";

	private DeserializerContext context;
	private SerializedObject serialized;
	private LocalVariable var;

	public BuilderConstruction(DeserializerContext context, LocalVariable var, SerializedObject value) {
		this.context = context;
		this.var = var;
		this.serialized = value;
	}

	public Computation build(TypeManager types, Deserializer generator) throws ReflectiveOperationException {
		Class<?> builderClass = context.getHint(serialized, Builder.class).orElseThrow(() -> new InstantiationException()).builder();

		BuilderModel model = assertBuilderConventions(builderClass);

		return model.build(types, generator);
	}

	private BuilderModel assertBuilderConventions(Class<?> builderClass) throws ReflectiveOperationException {
		builderClass.getConstructor();

		List<WithSetter> withSetters = new ArrayList<>();
		for (SerializedField field : serialized.getFields()) {
			String withSetter = withSetterNameFor(field.getName());
			Method method = getDeclaredMethod(builderClass, withSetter, baseType(field.getType()));
			if (method.getReturnType() != builderClass) {
				throw new NoSuchMethodException();
			}
			withSetters.add(new WithSetter(withSetter, field.getValue()));
		}

		Method builderMethod = builderClass.getMethod(BUILD);
		if (builderMethod.getReturnType() != serialized.getType()) {
			throw new NoSuchMethodException();
		}
		return new BuilderModel(var, builderClass, withSetters);
	}

	private String withSetterNameFor(String name) {
		return WITH + toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private static class BuilderModel {

		private String name;
		private Type type;
		private Type builderClass;
		private List<WithSetter> withSetters;

		BuilderModel(LocalVariable var, Type builderClass, List<WithSetter> withSetters) {
			this.name = var.getName();
			this.type = var.getType();
			this.builderClass = builderClass;
			this.withSetters = withSetters;
		}

		public Computation build(TypeManager types, Deserializer generator) {
			types.registerTypes(builderClass);

			List<String> statements = new ArrayList<>();

			String aggregate = newObject(types.getVariableTypeName(builderClass));

			for (WithSetter withSetter : withSetters) {
				Computation fieldComputation = withSetter.value.accept(generator);
				statements.addAll(fieldComputation.getStatements());
				aggregate = callMethod(aggregate, withSetter.method, fieldComputation.getValue());
			}

			aggregate = callMethod(aggregate, BUILD);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, aggregate));

			return variable(name, type, statements);
		}

	}

	private static class WithSetter {

		public String method;
		public SerializedValue value;

		WithSetter(String method, SerializedValue value) {
			this.method = method;
			this.value = value;
		}

	}
}
