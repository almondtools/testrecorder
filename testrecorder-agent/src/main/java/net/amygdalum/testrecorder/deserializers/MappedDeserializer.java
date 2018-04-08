package net.amygdalum.testrecorder.deserializers;

import java.util.function.Function;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValueType;

public class MappedDeserializer<T, S> implements Deserializer<T>{

	private Deserializer<S> visitor;
	private Function<S, T> mapping;

	public MappedDeserializer(Deserializer<S> visitor, Function<S,T> mapping) {
		this.visitor = visitor;
		this.mapping = mapping;
	}
	
	@Override
	public T visitField(SerializedFieldType field, DeserializerContext context) {
		return mapping.apply(field.accept(visitor, context));
	}
	
	@Override
	public T visitReferenceType(SerializedReferenceType value, DeserializerContext context) {
		return mapping.apply(value.accept(visitor, context));
	}

	@Override
	public T visitImmutableType(SerializedImmutableType value, DeserializerContext context) {
		return mapping.apply(value.accept(visitor, context));
	}

	@Override
	public T visitValueType(SerializedValueType value, DeserializerContext context) {
		return mapping.apply(value.accept(visitor, context));
	}

}
