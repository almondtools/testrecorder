package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.types.Computation.variable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.runtime.GenericObject;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.types.TypeManager;

public class SetupGenerators implements Deserializer<Computation> {

	private Adaptors<SetupGenerators> adaptors;

	public SetupGenerators(AgentConfiguration config) {
		this.adaptors = new Adaptors<SetupGenerators>(config).load(SetupGenerator.class);
	}

	@Override
	public Computation visitField(SerializedFieldType field, DeserializerContext context) {
		TypeManager types = context.getTypes();
		Type fieldType = field.getType();
		Type usedType = types.mostSpecialOf(field.getValue().getUsedTypes()).orElse(Object.class);
		Type fieldResultType = types.bestType(fieldType, Object.class);
		types.registerTypes(fieldType, usedType, fieldResultType);

		SerializedValue value = field.getValue();
		if (value instanceof SerializedReferenceType) {
			((SerializedReferenceType) value).useAs(fieldResultType);
		}
		Computation valueTemplate = value.accept(this, context.newWithHints(field.getAnnotations()));

		List<String> statements = valueTemplate.getStatements();

		String expression = valueTemplate.getValue();

		expression = context.adapt(expression, fieldResultType, valueTemplate.getType());

		String assignField = assignLocalVariableStatement(types.getVariableTypeName(fieldResultType), field.getName(), expression);
		return expression(assignField, null, statements);
	}

	@Override
	public Computation visitReferenceType(SerializedReferenceType value, DeserializerContext context) {
		TypeManager types = context.getTypes();
		if (context.defines(value)) {
			LocalVariable definition = context.getDefinition(value);
			String name = definition.getName();
			if (definition.isDefined()) {
				return variable(name, definition.getType());
			} else {
				List<String> statements = new ArrayList<>();
				String forwardExpression = callMethod(types.getVariableTypeName(GenericObject.class), "forward", types.getRawClass(value.getType()));
				Type resultType = types.wrapHidden(value.getType());
				statements.add(assignLocalVariableStatement(types.getRawTypeName(resultType), name, forwardExpression));
				definition.define(resultType);
				return variable(name, resultType, statements);
			}
		}
		return adaptors.tryDeserialize(value, types, this, context);
	}

	@Override
	public Computation visitValueType(SerializedValueType value, DeserializerContext context) {
		TypeManager types = context.getTypes();
		return adaptors.tryDeserialize(value, types, this, context);
	}

	@Override
	public Computation visitImmutableType(SerializedImmutableType value, DeserializerContext context) {
		TypeManager types = context.getTypes();
		return adaptors.tryDeserialize(value, types, this, context);
	}

}
