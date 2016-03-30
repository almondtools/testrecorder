package net.amygdalum.testrecorder.deserializers;

import java.util.function.Function;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.values.SerializedField;

public class MappedDeserializer<T, S> implements Deserializer<T>{

	private Deserializer<S> visitor;
	private Function<S, T> mapping;

	public MappedDeserializer(Deserializer<S> visitor, Function<S,T> mapping) {
		this.visitor = visitor;
		this.mapping = mapping;
	}
	
	@Override
	public T visitField(SerializedField field) {
		return mapping.apply(field.accept(visitor));
	}
	
	@Override
	public T visitReferenceType(SerializedReferenceType value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitImmutableType(SerializedImmutableType value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitValueType(SerializedValueType value) {
		return mapping.apply(value.accept(visitor));
	}

}
