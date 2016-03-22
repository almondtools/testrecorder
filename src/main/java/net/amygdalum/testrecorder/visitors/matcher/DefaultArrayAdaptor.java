package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.visitors.Templates.arrayContainingMatcher;
import static net.amygdalum.testrecorder.visitors.Templates.arrayEmptyMatcher;
import static net.amygdalum.testrecorder.visitors.Templates.primitiveArrayContainingMatcher;
import static net.amygdalum.testrecorder.visitors.TypeManager.isPrimitive;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;
import static net.amygdalum.testrecorder.visitors.TypeManager.wildcard;

import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsArrayContainingInOrder;
import org.hamcrest.collection.IsArrayWithSize;

import net.amygdalum.testrecorder.util.PrimitiveArrayMatcher;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultArrayAdaptor extends DefaultAdaptor<SerializedArray, ObjectToMatcherCode> implements Adaptor<SerializedArray, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedArray value, TypeManager types, ObjectToMatcherCode generator) {
		if (isPrimitive(value.getComponentType())) {
			String name = value.getComponentType().getTypeName();
			types.staticImport(PrimitiveArrayMatcher.class, name + "ArrayContaining");

			List<Computation> elements = Stream.of(value.getArray())
				.map(element -> generator.simpleValue(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String primitiveArrayContainingMatcher = primitiveArrayContainingMatcher(name, elementValues);
			return new Computation(primitiveArrayContainingMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		} else {
			if (value.getArrayAsList().isEmpty()) {
				types.staticImport(IsArrayWithSize.class, "emptyArray");

				String arrayEmptyMatcher = arrayEmptyMatcher();
				return new Computation(arrayEmptyMatcher, parameterized(Matcher.class, null, wildcard()));
			} else {
				types.staticImport(IsArrayContainingInOrder.class, "arrayContaining");

				List<Computation> elements = Stream.of(value.getArray())
					.map(element -> generator.simpleValue(element))
					.collect(toList());

				List<String> elementComputations = elements.stream()
					.flatMap(element -> element.getStatements().stream())
					.collect(toList());

				String[] elementValues = elements.stream()
					.map(element -> element.getValue())
					.toArray(String[]::new);

				String arrayContainingMatcher = arrayContainingMatcher(elementValues);
				return new Computation(arrayContainingMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
			}
		}
	}

}
