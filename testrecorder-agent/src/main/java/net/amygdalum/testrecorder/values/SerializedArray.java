package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.boxedType;
import static net.amygdalum.testrecorder.util.Types.component;
import static net.amygdalum.testrecorder.util.Types.mostSpecialOf;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedArray is restricted to arrays of any variant. It is recommended not to use another serialized array implementation. 
 */
public class SerializedArray extends AbstractSerializedReferenceType implements SerializedReferenceType {

	private List<SerializedValue> array;

	public SerializedArray(Class<?> type) {
		super(type);
		this.array = new ArrayList<>();
	}

	public SerializedArray with(Collection<SerializedValue> values) {
		array.addAll(values);
		return this;
	}

	public SerializedArray with(SerializedValue... values) {
		return with(asList(values));
	}

	public Type getComponentType() {
		Type[] candidates = Stream.concat(Stream.of(getType()), Arrays.stream(getUsedTypes()))
			.map(type -> component(type))
			.filter(this::satisfiesComponentType)
			.toArray(Type[]::new);
		return mostSpecialOf(candidates)
			.orElse(Object.class);
	}

	public boolean satisfiesComponentType(Type type) {
		Class<?> baseType = boxedType(type);
		return array.stream()
			.filter(value -> value.getType() != null)
			.allMatch(value -> baseType.isAssignableFrom(boxedType(value.getType())));
	}

	public Class<?> getRawType() {
		return baseType(getComponentType());
	}

	public SerializedValue[] getArray() {
		return array.toArray(new SerializedValue[0]);
	}

	public List<SerializedValue> getArrayAsList() {
		return array;
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return new ArrayList<>(array);
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	public void add(SerializedValue value) {
		array.add(value);
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
