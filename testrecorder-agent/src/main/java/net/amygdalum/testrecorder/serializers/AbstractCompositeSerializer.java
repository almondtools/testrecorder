package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isLiteral;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.util.Types.serializableOf;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public abstract class AbstractCompositeSerializer {

	public SerializedField resolvedFieldOf(SerializerSession session, Object object, Class<?> type, String name) {
		try {
			Field field = type.getDeclaredField(name);
			return resolvedFieldOf(session, object, field);
		} catch (ReflectiveOperationException e) {
			throw new SerializationException(e);
		}
	}

	public SerializedField resolvedFieldOf(SerializerSession session, Object object, Field field) {
		Class<?> declaringClass = field.getDeclaringClass();
		String name = field.getName();
		Type type = serializableOf(field.getGenericType());
		Object value = fieldOf(object, field);
		SerializedValue serializedValue = resolvedValueOf(session, type, value);

		return new SerializedField(declaringClass, name, type, serializedValue);
	}

	public SerializedValue resolvedValueOf(SerializerSession session, Type type, Object value) {
		SerializedValue serializedValue = session.ref(value, type);
		if (serializedValue != null) {
			return serializedValue;
		}
		Class<?> clazz = type == null ? null : baseType(type);
		if (value == null) {
			SerializedNull nullInstance = SerializedNull.nullInstance();
			if (clazz != null && !clazz.isSynthetic()) {
				nullInstance.useAs(type);
			}
			return nullInstance;
		} 
		if (isPrimitive(clazz)) {
			return SerializedLiteral.literal(clazz, value);
		}
		if (isLiteral(value.getClass())) {
			return SerializedLiteral.literal(value);
		} else {
			throw new SerializationException("cannot resolve value of type " + value.getClass().getName());
		}
	}

	public Object fieldOf(Object object, Class<?> type, String name) {
		try {
			Field field = type.getDeclaredField(name);
			return fieldOf(object, field);
		} catch (ReflectiveOperationException e) {
			throw new SerializationException(e);
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
