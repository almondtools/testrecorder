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
		String name = var.getName();
		Type type = var.getType();

		Class<?> builderClass = context.getHint(serialized, Builder.class).orElseThrow(() -> new InstantiationException()).builder();
		assertBuilderConventions(builderClass);
		
		types.registerTypes(builderClass);

		List<String> statements = new ArrayList<>();

		String aggregate = newObject(types.getVariableTypeName(builderClass));
		
		for (SerializedField field : serialized.getFields()) {
			String withSetter = withSetterNameFor(field.getName());
			Computation fieldComputation = field.getValue().accept(generator);
			statements.addAll(fieldComputation.getStatements());
			aggregate = callMethod(aggregate, withSetter, fieldComputation.getValue());
		}
		
		aggregate = callMethod(aggregate, BUILD);
		
		statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, aggregate));
		
		return variable(name, type, statements);
	}

	private void assertBuilderConventions(Class<?> builderClass) throws ReflectiveOperationException {
		builderClass.getConstructor();
		
		for (SerializedField field : serialized.getFields()) {
			String withSetter = withSetterNameFor(field.getName());
			Method method = getDeclaredMethod(builderClass, withSetter, baseType(field.getType()));
			if (method.getReturnType() != builderClass) {
				throw new NoSuchMethodException();
			}
		}
		
		Method builderMethod = builderClass.getMethod(BUILD);
		if (builderMethod.getReturnType() != serialized.getType()) {
			throw new NoSuchMethodException();
		}
	}

	private String withSetterNameFor(String name) {
		return WITH + toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
