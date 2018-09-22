package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldDeclaration;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.Types.serializableOf;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.runtime.GenericObject;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.types.TypeManager;

public class SetupGenerators implements DeserializerFactory {

	private Adaptors adaptors;

	public SetupGenerators(Adaptors adaptors) {
		this.adaptors = adaptors;
	}

	@Override
	public Generator newGenerator(DeserializerContext context) {
		return new Generator(adaptors, context);
	}

	public static class Generator implements Deserializer {

		private Adaptors adaptors;
		private DeserializerContext context;

		public Generator(Adaptors adaptors, DeserializerContext context) {
			this.adaptors = adaptors;
			this.context = context;
		}

		@Override
		public DeserializerContext getContext() {
			return context;
		}

		@Override
		public Computation visitField(SerializedField field) {
			return context.withRole(field, this::generateField);
		}

		private Computation generateField(SerializedField field) {
			TypeManager types = context.getTypes();
			Type fieldType = field.getType();
			Type usedType = types.mostSpecialOf(field.getValue().getUsedTypes()).orElse(Object.class);
			Type fieldResultType = types.bestType(fieldType, Object.class);
			types.registerTypes(fieldType, usedType, fieldResultType);

			SerializedValue value = field.getValue();
			if (value instanceof SerializedReferenceType) {
				((SerializedReferenceType) value).useAs(serializableOf(fieldResultType));
			}
			Computation valueTemplate = value.accept(this);

			List<String> statements = valueTemplate.getStatements();

			String expression = valueTemplate.getValue();

			expression = context.adapt(expression, fieldResultType, valueTemplate.getType());

			String assignField = fieldDeclaration(null, types.getVariableTypeName(fieldResultType), field.getName(), expression);
			return expression(assignField, null, statements);
		}

		@Override
		public Computation visitArgument(SerializedArgument argument) {
			return context.withRole(argument, this::generateArgument);
		}

		private Computation generateArgument(SerializedArgument argument) {
			SerializedValue argumentValue = argument.getValue();
			return argumentValue.accept(this);
		}

		@Override
		public Computation visitResult(SerializedResult result) {
			return context.withRole(result, this::generateResult);
		}

		private Computation generateResult(SerializedResult result) {
			SerializedValue resultValue = result.getValue();
			return resultValue.accept(this);
		}

		@Override
		public Computation visitReferenceType(SerializedReferenceType value) {
			return context.withRole(value, this::generateReferenceType);
		}

		private Computation generateReferenceType(SerializedReferenceType value) {
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
		public Computation visitValueType(SerializedValueType value) {
			return context.withRole(value, this::generateValueType);
		}

		private Computation generateValueType(SerializedValueType value) {
			TypeManager types = context.getTypes();
			return adaptors.tryDeserialize(value, types, this, context);
		}

		@Override
		public Computation visitImmutableType(SerializedImmutableType value) {
			return context.withRole(value, this::generateImmutableType);
		}

		private Computation generateImmutableType(SerializedImmutableType value) {
			TypeManager types = context.getTypes();
			return adaptors.tryDeserialize(value, types, this, context);
		}
	}
}
