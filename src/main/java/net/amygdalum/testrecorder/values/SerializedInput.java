package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

public class SerializedInput {

	private int id;
	private Class<?> clazz;
	private String name;
	private Type resultType;
	private SerializedValue result;
	private Type[] types;
	private SerializedValue[] values;

	public SerializedInput(int id, Class<?> clazz, String name, Type resultType, SerializedValue result, Type[] types, SerializedValue... values) {
		this.id = id;
		this.clazz = clazz;
		this.name = name;
		this.resultType = resultType;
		this.result = result;
		this.types = types;
		this.values = values;
	}

	public SerializedInput(int id, Class<?> clazz, String name, Type[] types, SerializedValue... values) {
		this.id = id;
		this.clazz = clazz;
		this.resultType = void.class;
		this.result = null;
		this.name = name;
		this.types = types;
		this.values = values;
	}
	
	public int getId() {
		return id;
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
		return "<< " + clazz.getTypeName() + "@" + id + "." + name + "(" + Optional.ofNullable(result).map(r -> r.accept(printer)).orElse("void") + ", " + Stream.of(values)
			.map(value -> value.accept(printer))
			.collect(joining(", ")) + ")";
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 31
			+ name.hashCode() * 19
			+ resultType.hashCode() * 7
			+ (result == null ? 0 : result.hashCode() * 3)
			+ Arrays.hashCode(types) * 17
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
		SerializedInput that = (SerializedInput) obj;
		return this.id == that.id
			&& this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.resultType.equals(that.resultType)
			&& Objects.equals(this.result,that.result)
			&& Arrays.equals(this.types, that.types)
			&& Arrays.equals(this.values, that.values);
	}

}
