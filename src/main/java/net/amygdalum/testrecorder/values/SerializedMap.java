package net.amygdalum.testrecorder.values;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedMap is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.Map (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has a put method that is sequence invariant (deserializers potentially call the put method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedMap implements SerializedReferenceType, Map<SerializedValue, SerializedValue> {

	private Type type;
	private Class<?> valueType;
	private Map<SerializedValue, SerializedValue> map;

	public SerializedMap(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
		this.map = new LinkedHashMap<>();
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public Class<?> getValueType() {
		return valueType;
	}

	public Type getMapKeyType() {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		} else {
			return Object.class;
		}
	}

	public Type getMapValueType() {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[1];
		} else {
			return Object.class;
		}
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
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
	public String toString() {
		return accept(new ValuePrinter());
	}

}
