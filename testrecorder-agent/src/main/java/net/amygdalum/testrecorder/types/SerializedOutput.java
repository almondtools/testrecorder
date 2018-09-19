package net.amygdalum.testrecorder.types;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

public class SerializedOutput extends AbstractSerializedInteraction implements SerializedInteraction {

	public SerializedOutput(int id, MethodSignature signature) {
		super(id, signature);
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
		return ">> " + signature.declaringClass.getTypeName() + "@" + id + "." + signature.methodName + argumentsStr;
	}

}
