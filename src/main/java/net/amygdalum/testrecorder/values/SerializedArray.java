package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedArray is restricted to arrays of any variant. It is recommended not to use another serialized array implementation. 
 */
public class SerializedArray implements SerializedReferenceType {

	private Type type;
	private Class<?> valueType;
	private List<SerializedValue> array;

	public SerializedArray(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
		this.array = new ArrayList<>();
	}

	public SerializedArray with(SerializedValue... values) {
		array.addAll(asList(values));
		return this;
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
	
	public Type getComponentType() {
		if (type instanceof Class<?> && ((Class<?>) type).isArray()) {
			return ((Class<?>) type).getComponentType();
		} else if (type instanceof GenericArrayType) {
			return ((GenericArrayType) type).getGenericComponentType();
		} else {
			return Object.class;
		}
	}

	public Class<?> getRawType() {
		if (type instanceof Class<?> && ((Class<?>) type).isArray()) {
			return ((Class<?>) type).getComponentType();
		} else {
			return Object.class;
		}
	}

	public SerializedValue[] getArray() {
		return array.toArray(new SerializedValue[0]);
	}

	public List<SerializedValue> getArrayAsList() {
		return array;
	}
	
	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
	}
	
	public void add(SerializedValue value) {
		array.add(value);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
