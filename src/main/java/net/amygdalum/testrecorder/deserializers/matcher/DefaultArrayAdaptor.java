package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Computation.expression;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayContainingMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayEmptyMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.primitiveArrayContainingMatcher;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsArrayWithSize;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.runtime.ArrayMatcher;
import net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedArray;

public class DefaultArrayAdaptor extends DefaultMatcherGenerator<SerializedArray> implements MatcherGenerator<SerializedArray> {

	@Override
	public Class<SerializedArray> getAdaptedClass() {
		return SerializedArray.class;
	}

	@Override
	public Computation tryDeserialize(SerializedArray value, MatcherGenerators generator, DeserializerContext context) {
	    Type componentType = value.getComponentType();

	    TypeManager types = generator.getTypes();
        if (types.isHidden(componentType)) {
            componentType = Object.class;
        }

		if (isPrimitive(componentType)) {
			String name = componentType.getTypeName();
			types.staticImport(PrimitiveArrayMatcher.class, name + "ArrayContaining");

			List<Computation> elements = Stream.of(value.getArray())
				.map(element -> generator.simpleMatcher(element, context))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String primitiveArrayContainingMatcher = primitiveArrayContainingMatcher(name, elementValues);
			return expression(primitiveArrayContainingMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		} else {
			if (value.getArrayAsList().isEmpty()) {
				types.staticImport(IsArrayWithSize.class, "emptyArray");

				String arrayEmptyMatcher = arrayEmptyMatcher();
				return expression(arrayEmptyMatcher, parameterized(Matcher.class, null, wildcard()));
			} else {
				types.staticImport(ArrayMatcher.class, "arrayContaining");
				String name = types.getRawTypeName(componentType);

				List<Computation> elements = Stream.of(value.getArray())
					.map(element -> generator.simpleMatcher(element, context))
					.collect(toList());

				List<String> elementComputations = elements.stream()
					.flatMap(element -> element.getStatements().stream())
					.collect(toList());

				String[] elementValues = elements.stream()
					.map(element -> element.getValue())
					.toArray(String[]::new);

				String arrayContainingMatcher = arrayContainingMatcher(name, elementValues);
				return expression(arrayContainingMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
			}
		}
	}

}
