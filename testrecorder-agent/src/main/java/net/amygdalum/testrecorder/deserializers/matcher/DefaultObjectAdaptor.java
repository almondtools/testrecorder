package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.FieldNamingStrategy.ensureUniqueNames;
import static net.amygdalum.testrecorder.deserializers.Templates.genericObjectMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.runtime.GenericMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultObjectAdaptor extends DefaultMatcherGenerator<SerializedObject> implements MatcherGenerator<SerializedObject> {

	@Override
	public Class<SerializedObject> getAdaptedClass() {
		return SerializedObject.class;
	}

	@Override
	public Computation tryDeserialize(SerializedObject value, MatcherGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerTypes(value.getType(), GenericMatcher.class);
		types.registerTypes(value.getUsedTypes());

		List<Computation> fields = ensureUniqueNames(value.getFields()).stream()
			.sorted()
			.map(field -> field.accept(generator, context))
			.filter(Objects::nonNull)
			.collect(toList());

		List<String> fieldComputations = fields.stream()
			.flatMap(field -> field.getStatements().stream())
			.collect(toList());

		List<String> fieldAssignments = fields.stream()
			.map(field -> field.getValue())
			.collect(toList());

		Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
		Type matchedType = types.isHidden(usedType) ? wildcard() : usedType;
		Type resultType = parameterized(Matcher.class, null, matchedType);

		String matcherExpression = with(types).createMatcherExpression(value, fieldAssignments);

		return expression(matcherExpression, resultType, fieldComputations);
	}

	public TypesAware with(TypeManager types) {
		return new TypesAware(types);
	}

	private static class TypesAware {

		private TypeManager types;

		public TypesAware(TypeManager types) {
			this.types = types;
		}

		public String createMatcherExpression(SerializedObject value, List<String> fieldAssignments) {
			Type type = value.getType();
			Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(type);
			if (baseType(usedType) == Matcher.class) {
				usedType = typeArgument(usedType, 0).orElse(wildcard());
			}
			if (usedType.equals(type)) {
				String matcherRawType = types.getRawClass(type);
				return genericObjectMatcher(matcherRawType, fieldAssignments);
			} else {
				String matcherRawType = types.getRawClass(type);
				String matcherToType = types.getRawClass(usedType);
				return genericObjectMatcher(matcherRawType, matcherToType, fieldAssignments);
			}
		}

	}

}
