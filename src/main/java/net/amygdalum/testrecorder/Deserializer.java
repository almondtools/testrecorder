package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedField;

public interface Deserializer<T> {

    default T visitField(SerializedField field) {
        return visitField(field, DeserializerContext.NULL);
    }
	
    T visitField(SerializedField field, DeserializerContext context);

	default T visitReferenceType(SerializedReferenceType value) {
	    return visitReferenceType(value, DeserializerContext.NULL);
	}

	T visitReferenceType(SerializedReferenceType value, DeserializerContext context);

    default T visitImmutableType(SerializedImmutableType value){
        return visitImmutableType(value, DeserializerContext.NULL);
    }

	T visitImmutableType(SerializedImmutableType value, DeserializerContext context);

	default T visitValueType(SerializedValueType value){
        return visitValueType(value, DeserializerContext.NULL);
    }
    
	T visitValueType(SerializedValueType value, DeserializerContext context);

}
