package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

public class SerializedOutput {

	private int id;
	private Class<?> clazz;
	private String name;
	private Type[] types;
	private SerializedValue[] values;

	public SerializedOutput(int id, Class<?> clazz, String name, Type[] types, SerializedValue... values) {
		this.id = id;
		this.clazz = clazz;
		this.name = name;
		this.types = types;
		this.values = values;
	}
	
	public int getId() {
		return id;
	}

	public String getSignature() {
		return clazz.getName() + "." + name + Arrays.stream(types)
			.map(type -> baseType(type).getName())
			.collect(joining(",", "(", ")"));
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
		ValuePrinter printer = new ValuePrinter();
		return ">> " + clazz.getTypeName() + "@" + id + "." + name + Stream.of(values)
			.map(value -> value.accept(printer))
			.collect(joining(", ", "(", ")"));
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 37
			+ name.hashCode() * 29
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
			&& this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& Arrays.equals(this.types, that.types)
			&& Arrays.equals(this.values, that.values);
	}

}
