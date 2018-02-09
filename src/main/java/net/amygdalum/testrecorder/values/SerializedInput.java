package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedInteraction;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedInput extends AbstractSerializedInteraction implements SerializedInteraction {

	public SerializedInput(int id, Class<?> clazz, String name, Type resultType, Type[] types) {
		super(id, clazz, name, resultType, types);
	}

	public SerializedInput updateArguments(SerializedValue... arguments) {
		this.arguments = arguments;
		return this;
	}

	public SerializedInput updateResult(SerializedValue result) {
		this.result = result;
		return this;
	}

	@Override
	public String toString() {
		ValuePrinter printer = new ValuePrinter();
		return "<< " + clazz.getTypeName() + "@" + id + "." + name + "(" + Optional.ofNullable(result).map(r -> r.accept(printer, printer)).orElse("void") + ", " + Stream.of(arguments)
			.map(value -> value.accept(printer, printer))
			.collect(joining(", ")) + ")";
	}

}
