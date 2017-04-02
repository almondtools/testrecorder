package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

public class SerializedField implements Comparable<SerializedField>{

	private String name;
	private Type type;
	private SerializedValue value;
	private Class<?> clazz;

    public SerializedField(Class<?> clazz, String name, Type type, SerializedValue value) {
		this.clazz = clazz;
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public SerializedValue getValue() {
		return value;
	}

	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitField(this);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}
	
	@Override
	public int compareTo(SerializedField o) {
		return name.compareTo(o.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode() * 31
			+ type.getTypeName().hashCode() * 13
			+ value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SerializedField that = (SerializedField) obj;
		return this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.type == that.type
			&& this.value.equals(that.value);
	}

}
