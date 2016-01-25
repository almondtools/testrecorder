package com.almondtools.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedInput {

	private Class<?> clazz;
	private String name;
	private Type resultType;
	private SerializedValue result;
	private Type[] types;
	private SerializedValue[] values;

	public SerializedInput(Class<?> clazz, String name, Type resultType, SerializedValue result, Type[] types, SerializedValue... values) {
		this.clazz = clazz;
		this.name = name;
		this.resultType = resultType;
		this.result = result;
		this.types = types;
		this.values = values;
	}

	public SerializedInput(Class<?> clazz, String name, Type[] types, SerializedValue... values) {
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
	
	public Type getResultType() {
		return resultType;
	}
	
	public SerializedValue getResult() {
		return result;
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
		return "<< " + clazz.getTypeName() + "." + name + "(" + result.accept(printer) + ", " + Stream.of(values)
			.map(value -> value.accept(printer))
			.collect(joining(", ")) + ")";
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 31 
			+ name.hashCode() * 19
			+ resultType.hashCode() * 7
			+ result.hashCode() * 3
			+ types.hashCode() * 17
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
		SerializedInput that = (SerializedInput) obj;
		return this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.resultType.equals(that.resultType)
			&& this.result.equals(that.result)
			&& this.types.equals(that.types)
			&& this.values.equals(that.values);
	}

}
