package com.almondtools.testrecorder.values;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedOutput {

	private Class<?> clazz;
	private String name;
	private SerializedValue[] values;

	public SerializedOutput(Class<?> clazz, String name, SerializedValue... values) {
		this.clazz = clazz;
		this.name = name;
		this.values = values;
	}
	
	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public SerializedValue[] getValues() {
		return values;
	}

	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitOutput(this);
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() * 29
			+ values.hashCode();
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
		SerializedOutput that = (SerializedOutput) obj;
		return this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.values.equals(that.values);
	}


}
