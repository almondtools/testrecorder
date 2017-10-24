package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

public class SerializedOutput {

	private int id;
	private String caller;
	private Class<?> clazz;
	private String name;
	private Type resultType;
	private SerializedValue result;
	private Type[] types;
	private SerializedValue[] values;

	public SerializedOutput(int id, String caller, Class<?> clazz, String name, Type resultType, SerializedValue result, Type[] types, SerializedValue... values) {
		this.id = id;
		this.caller = caller;
		this.clazz = clazz;
		this.name = name;
		this.resultType = resultType;
		this.result = result;
		this.types = types;
		this.values = values;
	}

	public SerializedOutput(int id, String caller, Class<?> clazz, String name, Type[] types, SerializedValue... values) {
		this.id = id;
		this.caller = caller;
		this.clazz = clazz;
		this.name = name;
		this.resultType = void.class;
		this.result = null;
		this.types = types;
		this.values = values;
	}
	
	public int getId() {
		return id;
	}

	public String getCaller() {
		return caller;
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
		ValuePrinter printer = new ValuePrinter();
		return ">> " + clazz.getTypeName() + "@" + id + "." + name + Stream.of(values)
			.map(value -> value.accept(printer))
			.collect(joining(", ", "(", ")"));
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 37
			+ name.hashCode() * 29
			+ resultType.hashCode() * 17
			+ (result == null ? 0 : result.hashCode() * 13)
			+ Arrays.hashCode(types) * 11
			+ Arrays.hashCode(values);
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
		return this.id == that.id
			&& this.caller.equals(that.caller)
			&& this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.resultType.equals(that.resultType)
			&& Objects.equals(this.result,that.result)
			&& Arrays.equals(this.types, that.types)
			&& Arrays.equals(this.values, that.values);
	}

}
