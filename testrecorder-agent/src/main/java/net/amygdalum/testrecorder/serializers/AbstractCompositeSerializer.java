package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.isLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public abstract class AbstractCompositeSerializer {

	public SerializedField serializedFieldOf(SerializerSession session, Object object, Field field) {
		Class<?> declaringClass = field.getDeclaringClass();
		String name = field.getName();
		Type type = Types.serializableOf(field.getGenericType());
		Object value = fieldOf(object, field);
		SerializedValue serializedValue = serializedValueOf(session, type, value);

		return new SerializedField(declaringClass, name, type, serializedValue);
	}

	public SerializedValue serializedValueOf(SerializerSession session, Type type, Object value) {
		SerializedValue serializedValue = serializedValueOf(session, type == null ? null : Types.baseType(type), value);
		if (serializedValue instanceof SerializedReferenceType) {
			((SerializedReferenceType) serializedValue).useAs(type);
		}
		return serializedValue;
	}

	private SerializedValue serializedValueOf(SerializerSession session, Class<?> type, Object value) {
		if (value == null) {
			return SerializedNull.nullInstance();
		} 
		if (Types.isPrimitive(type)) {
			return SerializedLiteral.literal(type, value);
		}
		if (isLiteral(value.getClass())) {
			return SerializedLiteral.literal(value);
		} else {
			return session.find(value);
		}
	}

	public Object fieldOf(Object object, Field field) {
		try {
			return accessing(field).call(f -> f.get(object));
		} catch (ReflectiveOperationException e) {
			throw new SerializationException(e);
		}
	}

}
