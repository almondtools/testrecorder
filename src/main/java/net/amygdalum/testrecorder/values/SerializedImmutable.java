package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;
import net.amygdalum.testrecorder.Deserializer;

public class SerializedImmutable<V> implements SerializedImmutableType {

	private Type type;
	private Class<?> valueType;
	private V value;

	public SerializedImmutable(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
	}

	public SerializedImmutable<V> withValue(V value) {
		this.value = value;
		return this;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public Class<?> getValueType() {
		return valueType;
	}

	public V getValue() {
		return value;
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
