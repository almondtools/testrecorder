package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.genericObjectMatcher;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.GenericMatcher;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultObjectAdaptor extends DefaultAdaptor<SerializedObject, ObjectToMatcherCode> implements Adaptor<SerializedObject, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedObject value, ObjectToMatcherCode generator) {
		TypeManager types = generator.getTypes();
		types.registerTypes(value.getResultType(), value.getType(), GenericMatcher.class);

		List<Computation> fields = value.getFields().stream()
			.sorted()
			.map(field -> field.accept(generator))
			.collect(toList());

		List<String> fieldComputations = fields.stream()
			.flatMap(field -> field.getStatements().stream())
			.collect(toList());

		List<String> fieldAssignments = fields.stream()
			.map(field -> field.getValue())
			.collect(toList());

		Type matchedType = types.isHidden(value.getResultType()) ? wildcard() : value.getResultType();
		Type resultType = parameterized(Matcher.class, null, matchedType);

		String matcherExpression = with(types).createMatcherExpression(value, fieldAssignments);

		return new Computation(matcherExpression, resultType, fieldComputations);
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
			Type resultType = value.getResultType();
			if (baseType(resultType) == Matcher.class) {
				resultType = typeArgument(resultType, 0).orElse(wildcard());
			}
			Type type = value.getType();
			if (resultType.equals(type)) {
				String matcherRawType = types.getRawTypeName(type);
				return genericObjectMatcher(matcherRawType, fieldAssignments);
			} else {
				String matcherRawType = types.getRawTypeName(type);
				String matcherToType = types.getRawTypeName(resultType);
				return genericObjectMatcher(matcherRawType, matcherToType, fieldAssignments);
			}
		}

	}

}
