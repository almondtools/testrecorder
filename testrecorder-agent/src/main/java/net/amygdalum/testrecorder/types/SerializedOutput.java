package net.amygdalum.testrecorder.types;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public class SerializedOutput extends AbstractSerializedInteraction implements SerializedInteraction {

	public SerializedOutput(int id, Class<?> clazz, String name, Type resultType, Type[] types) {
		super(id, clazz, name, resultType, types);
	}

	public SerializedOutput updateArguments(SerializedValue... arguments) {
		this.arguments = argumentsOf(arguments);
		return this;
	}

	public SerializedOutput updateResult(SerializedValue result) {
		this.result = resultOf(result);
		return this;
	}

	@Override
	public String toString() {
		String argumentsStr = Stream.of(arguments)
			.map(Object::toString)
			.collect(joining(", ", "(", ")"));
		return ">> " + clazz.getTypeName() + "@" + id + "." + name + argumentsStr;
	}

}
