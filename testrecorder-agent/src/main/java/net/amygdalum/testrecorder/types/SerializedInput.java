package net.amygdalum.testrecorder.types;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

public class SerializedInput extends AbstractSerializedInteraction implements SerializedInteraction {

	public SerializedInput(int id, Class<?> clazz, String name, Type resultType, Type[] types) {
		super(id, clazz, name, resultType, types);
	}

	public SerializedInput updateArguments(SerializedValue... arguments) {
		this.arguments = argumentsOf(arguments);
		
		return this;
	}

	public SerializedInput updateResult(SerializedValue result) {
		this.result = resultOf(result);
		return this;
	}

	@Override
	public String toString() {
		String resultStr = Optional.ofNullable(result)
			.map(value -> value.toString())
			.orElse("void");
		String argumentsStr = Stream.of(arguments)
			.map(value -> value.toString())
			.collect(joining(", "));
		return "<< " + clazz.getTypeName() + "@" + id + "." + name + "(" + resultStr + ", " + argumentsStr + ")";
	}

}
