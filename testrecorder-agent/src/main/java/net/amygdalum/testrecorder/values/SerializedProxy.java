package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedProxy extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private List<SerializedImmutable<Class<?>>> interfaces;
	private SerializedValue invocationHandler;
	private List<SerializedField> fields;

	public SerializedProxy(Type type) {
		super(Proxy.class);
		this.fields = new ArrayList<>();
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	public void setInterfaces(List<SerializedImmutable<Class<?>>> interfaces) {
		this.interfaces = interfaces;
	}
	
	public List<SerializedImmutable<Class<?>>> getInterfaces() {
		return interfaces;
	}

	public void setInvocationHandler(SerializedValue invocationHandler) {
		this.invocationHandler = invocationHandler;
	}
	
	public SerializedValue getInvocationHandler() {
		return invocationHandler;
	}

	public List<SerializedField> getFields() {
		return fields;
	}

	public Optional<SerializedField> getField(String name) {
		return fields.stream()
			.filter(field -> name.equals(field.getName()))
			.findFirst();
	}

	public void addField(SerializedField field) {
		fields.add(field);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		List<SerializedValue> referencedValues = new ArrayList<>();
		referencedValues.addAll(interfaces);
		referencedValues.add(invocationHandler);
		referencedValues.addAll(fields.stream()
			.map(field -> field.getValue())
			.collect(toList()));
		return referencedValues;
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
