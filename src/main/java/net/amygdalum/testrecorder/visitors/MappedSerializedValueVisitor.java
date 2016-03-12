package net.amygdalum.testrecorder.visitors;

import java.util.function.Function;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MappedSerializedValueVisitor<T, S> implements SerializedValueVisitor<T>{

	private SerializedValueVisitor<S> visitor;
	private Function<S, T> mapping;

	public MappedSerializedValueVisitor(SerializedValueVisitor<S> visitor, Function<S,T> mapping) {
		this.visitor = visitor;
		this.mapping = mapping;
	}
	
	@Override
	public T visitField(SerializedField field) {
		return mapping.apply(field.accept(visitor));
	}

	@Override
	public T visitObject(SerializedObject value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitArray(SerializedArray value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitLiteral(SerializedLiteral value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitNull(SerializedNull value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitUnknown(SerializedValue value) {
		return mapping.apply(value.accept(visitor));
	}

}
