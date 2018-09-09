package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.Templates.fieldDeclaration;
import static net.amygdalum.testrecorder.deserializers.Templates.recursiveMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.runtime.GenericMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedKeyValue;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.types.TypeManager;

public class MatcherGenerators implements DeserializerFactory {

	private Adaptors adaptors;

	public MatcherGenerators(AgentConfiguration config) {
		this(new Adaptors(config).load(MatcherGenerator.class));
	}

	public MatcherGenerators(Adaptors adaptors) {
		this.adaptors = adaptors;
	}

	public Generator newGenerator(DeserializerContext context) {
		return new Generator(adaptors, context);
	}

	public static class Generator implements Deserializer {

		private Adaptors adaptors;
		private SimpleValueAdaptor simpleAdaptor;
		private DeserializerContext context;

		public Generator(Adaptors adaptors, DeserializerContext context) {
			this.adaptors = adaptors;
			this.simpleAdaptor = new SimpleValueAdaptor();
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
			SerializedValue fieldValue = field.getValue();
			if (context.getHint(field, SkipChecks.class).isPresent()) {
				return null;
			} else if (simpleAdaptor.isSimpleValue(fieldValue)) {
				Type fieldType = field.getType();
				Type fieldResultType = types.bestType(fieldType, Object.class);
				types.registerImport(baseType(fieldResultType));
				Computation value = simpleAdaptor.tryDeserialize(fieldValue, this);

				String assignField = fieldDeclaration(null, types.getRawTypeName(fieldResultType), field.getName(), value.getValue());
				return expression(assignField, null, value.getStatements());
			} else {
				types.registerImport(Matcher.class);
				Computation value = fieldValue.accept(this);

				String genericType = types.getVariableTypeName(parameterized(Matcher.class, null, wildcard()));

				String assignField = fieldDeclaration(null, genericType, field.getName(), value.getValue());
				return expression(assignField, null, value.getStatements());
			}
		}

		@Override
		public Computation visitKeyValue(SerializedKeyValue keyvalue) {
			throw new DeserializationException("keyvalues are not used in matcher generation");
		}

		@Override
		public Computation visitArgument(SerializedArgument argument) {
			return context.withRole(argument, this::generateArgument);
		}

		private Computation generateArgument(SerializedArgument argument) {
			SerializedValue argumentValue = argument.getValue();
			if (context.getHint(argument, SkipChecks.class).isPresent()) {
				return null;
			} else {
				return argumentValue.accept(this);
			}
		}

		@Override
		public Computation visitResult(SerializedResult result) {
			return context.withRole(result, this::generateResult);
		}

		private Computation generateResult(SerializedResult result) {
			SerializedValue resultValue = result.getValue();
			if (context.getHint(result, SkipChecks.class).isPresent()) {
				return null;
			} else {
				return resultValue.accept(this);
			}
		}

		@Override
		public Computation visitReferenceType(SerializedReferenceType value) {
			return context.withRole(value, this::generateReferenceType);
		}

		private Computation generateReferenceType(SerializedReferenceType value) {
			TypeManager types = context.getTypes();
			Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
			if (context.getHint(value, SkipChecks.class).isPresent()) {
				return null;
			} else if (context.isComputed(value)) {
				types.staticImport(GenericMatcher.class, "recursive");
				Type resultType = usedType.equals(value.getType()) ? parameterized(Matcher.class, null, usedType) : parameterized(Matcher.class, null, wildcard());
				if (!types.isHidden(value.getType())) {
					return expression(recursiveMatcher(types.getRawClass(value.getType())), resultType);
				} else if (!types.isHidden(usedType)) {
					return expression(recursiveMatcher(types.getRawClass(usedType)), resultType);
				} else {
					return expression(recursiveMatcher(types.getRawClass(Object.class)), parameterized(Matcher.class, null, wildcard()));
				}
			}
			return adaptors.tryDeserialize(value, types, this, context);
		}

		@Override
		public Computation visitImmutableType(SerializedImmutableType value) {
			return context.withRole(value, this::generateImmutableType);
		}

		private Computation generateImmutableType(SerializedImmutableType value) {
			TypeManager types = context.getTypes();
			if (context.getHint(value, SkipChecks.class).isPresent()) {
				return null;
			}
			return adaptors.tryDeserialize(value, types, this, context);
		}

		@Override
		public Computation visitValueType(SerializedValueType value) {
			return context.withRole(value, this::generateValueType);
		}

		private Computation generateValueType(SerializedValueType value) {
			TypeManager types = context.getTypes();
			if (context.getHint(value, SkipChecks.class).isPresent()) {
				return null;
			}
			return adaptors.tryDeserialize(value, types, this, context);
		}
	}
}
