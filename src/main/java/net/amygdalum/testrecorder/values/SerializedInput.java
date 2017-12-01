package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedInteraction;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedInput implements SerializedInteraction {

	private int id;
	private StackTraceElement caller;
	private Class<?> clazz;
	private String name;
	private Type resultType;
	private SerializedValue result;
	private Type[] types;
	private SerializedValue[] arguments;

	public SerializedInput(int id, StackTraceElement caller, Class<?> clazz, String name, Type resultType, SerializedValue result, Type[] types, SerializedValue... arguments) {
		this.id = id;
		this.caller = caller;
		this.clazz = clazz;
		this.name = name;
		this.resultType = resultType;
		this.result = result;
		this.types = types;
		this.arguments = arguments;
	}

	public SerializedInput(int id, StackTraceElement caller, Class<?> clazz, String name, Type[] types, SerializedValue... arguments) {
		this.id = id;
		this.caller = caller;
		this.clazz = clazz;
		this.resultType = void.class;
		this.result = null;
		this.name = name;
		this.types = types;
		this.arguments = arguments;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getCallerClass() {
		return caller.getClassName();
	}
	
	@Override
	public String getCallerMethod() {
		return caller.getMethodName();
	}

	@Override
	public int getCallerLine() {
		return caller.getLineNumber();
	}

	@Override
	public Class<?> getDeclaringClass() {
		return clazz;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getResultType() {
		return resultType;
	}

	@Override
	public SerializedValue getResult() {
		return result;
	}

	@Override
	public Type[] getTypes() {
		return types;
	}

	@Override
	public SerializedValue[] getArguments() {
		return arguments;
	}

	@Override
	public List<SerializedValue> getAllValues() {
		List<SerializedValue> allValues = new ArrayList<>();
		allValues.add(result);
		for (SerializedValue argument : arguments) {
			allValues.add(argument);
		}
		return allValues;
	}

	@Override
	public String toString() {
		ValuePrinter printer = new ValuePrinter();
		return "<< " + clazz.getTypeName() + "@" + id + "." + name + "(" + Optional.ofNullable(result).map(r -> r.accept(printer, printer)).orElse("void") + ", " + Stream.of(arguments)
			.map(value -> value.accept(printer, printer))
			.collect(joining(", ")) + ")";
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 31
			+ name.hashCode() * 19
			+ resultType.hashCode() * 7
			+ (result == null ? 0 : result.hashCode() * 3)
			+ Arrays.hashCode(types) * 17
			+ Arrays.hashCode(arguments);
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
			&& this.caller.equals(that.caller)
			&& this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.resultType.equals(that.resultType)
			&& Objects.equals(this.result,that.result)
			&& Arrays.equals(this.types, that.types)
			&& Arrays.equals(this.arguments, that.arguments);
	}

}
