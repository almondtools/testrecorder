package com.almondtools.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedOutput {

	private Class<?> clazz;
	private String name;
	private Type[] types;
	private SerializedValue[] values;

	public SerializedOutput(Class<?> clazz, String name, Type[] types, SerializedValue... values) {
		this.clazz = clazz;
		this.name = name;
		this.types = types;
		this.values = values;
	}

	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}
	
	public Type[] getTypes() {
		return types;
	}

	public SerializedValue[] getValues() {
		return values;
	}

	@Override
	public String toString() {
		SerializedValuePrinter printer = new SerializedValuePrinter();
		return ">> " + clazz.getTypeName() + "." + name + Stream.of(values)
			.map(value -> value.accept(printer))
			.collect(joining(", ", "(", ")"));
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 37 
			+ name.hashCode() * 29
			+ types.hashCode() * 11
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
			&& this.types.equals(that.types)
			&& this.values.equals(that.values);
	}

}
