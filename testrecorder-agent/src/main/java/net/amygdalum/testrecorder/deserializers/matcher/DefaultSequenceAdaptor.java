package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.containsInOrderMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.emptyMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isGeneric;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.runtime.ContainsInOrderMatcher;
import net.amygdalum.testrecorder.runtime.ContainsMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultSequenceAdaptor extends DefaultMatcherGenerator<SerializedList> implements MatcherGenerator<SerializedList> {

	private SimpleValueAdaptor simpleAdaptor;

	public DefaultSequenceAdaptor() {
		this.simpleAdaptor = new SimpleValueAdaptor();
	}
	
	@Override
	public Class<SerializedList> getAdaptedClass() {
		return SerializedList.class;
	}

	@Override
	public Computation tryDeserialize(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		if (value.isEmpty()) {
			TypeManager types = context.getTypes();
			types.registerImport(baseType(componentType(types, value)));
			types.staticImport(ContainsMatcher.class, "empty");

			return expression(matchEmpty(context, value), parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			TypeManager types = context.getTypes();
			types.staticImport(ContainsInOrderMatcher.class, "containsInOrder");
			types.registerImport(baseType(componentType(types, value)));

			List<Computation> elements = value.stream()
				.map(element -> simpleAdaptor.tryDeserialize(element, generator))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String containsMatcher = matchElements(context, value, elementValues);

			return expression(containsMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

	private String matchElements(DeserializerContext context, SerializedList value, String[] elementValues) {
		TypeManager types = context.getTypes();
		if (hasErasedType(types, value)) {
			return containsInOrderMatcher(null, elementValues);
		} else {
			Type componentType = componentType(types, value);
			String elementType = types.getRawClass(componentType);
			if (isGeneric(componentType)) {
				elementType = context.adapt(elementType, parameterized(Class.class, null, componentType), parameterized(Class.class, null, wildcard()));
			}
			return containsInOrderMatcher(elementType, elementValues);
		}
	}

	private String matchEmpty(DeserializerContext context, SerializedList value) {
		TypeManager types = context.getTypes();
		if (hasErasedType(types, value)) {
			return emptyMatcher(null);
		} else {
			return emptyMatcher(types.getRawClass(componentType(types, value)));
		}
	}

	private boolean hasErasedType(TypeManager types, SerializedList value) {
		Type collectionType = types.mostSpecialOf(value.getUsedTypes()).orElse(value.getType());
		return componentType(types, value) == Object.class && collectionType instanceof Class<?>;
	}

	private Type componentType(TypeManager types, SerializedList value) {
		Type componentType = value.getComponentType();
		if (types.isHidden(componentType)) {
			componentType = Object.class;
		}
		return componentType;
	}

}
