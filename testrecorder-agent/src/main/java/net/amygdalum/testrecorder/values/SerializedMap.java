package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.mostSpecialOf;
import static net.amygdalum.testrecorder.util.Types.typeArgument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedMap is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.Map (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has a put method that is sequence invariant (deserializers potentially call the put method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedMap extends AbstractSerializedReferenceType implements SerializedReferenceType, Map<SerializedValue, SerializedValue> {

	private Map<SerializedValue, SerializedValue> map;

	public SerializedMap(Type type) {
		super(type);
		this.map = new LinkedHashMap<>();
	}

	public SerializedMap with(Map<SerializedValue, SerializedValue> values) {
		map.putAll(values);
		return this;
	}

	public Type getMapKeyType() {
		return typeArgument(getType(), 0)
			.orElse(typeArgument(mostSpecialOf(getUsedTypes()).orElse(Object.class), 0)
				.orElse(Object.class));
	}

	public Type getMapValueType() {
		return typeArgument(getType(), 1)
			.orElse(typeArgument(mostSpecialOf(getUsedTypes()).orElse(Object.class), 1)
				.orElse(Object.class));
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public SerializedValue get(Object key) {
		return map.get(key);
	}

	public SerializedValue put(SerializedValue key, SerializedValue value) {
		return map.put(key, value);
	}

	public SerializedValue remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map<? extends SerializedValue, ? extends SerializedValue> m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Set<SerializedValue> keySet() {
		return map.keySet();
	}

	public Collection<SerializedValue> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<SerializedValue, SerializedValue>> entrySet() {
		return map.entrySet();
	}

	@Override
	public List<SerializedValue> referencedValues() {
		List<SerializedValue> referenced = new ArrayList<>(map.keySet());
		referenced.addAll(map.values());
		return referenced;
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
