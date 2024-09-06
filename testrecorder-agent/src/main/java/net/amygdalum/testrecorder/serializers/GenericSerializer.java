package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class GenericSerializer extends AbstractCompositeSerializer implements Serializer<SerializedReferenceType> {

	public GenericSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		Builder<Object> components = Stream.builder();
		if (!session.facades(object)) {
			Class<?> objectClass = object.getClass();
			while (objectClass != Object.class && !session.excludes(objectClass)) {
				for (Field f : objectClass.getDeclaredFields()) {
					if (!session.excludes(f) && !session.facades(f)) {
						if (!isPrimitive(f.getType())) {
							components.add(fieldOf(object, f));
						}
					}
				}
				objectClass = objectClass.getSuperclass();
			}
		}
		return components.build();
	}

	@Override
	public SerializedReferenceType generate(Class<?> type, SerializerSession session) {
		Class<?> clazz = baseType(type);
        if (session.excludes(clazz)) {
			SerializedNull nullInstance = nullInstance();
			if (type != null) {
			    nullInstance.useAs(type);
			}
            return nullInstance;
		} else {
			return new SerializedObject(type);
		}
	}

	@Override
	public void populate(SerializedReferenceType serializedValue, Object object, SerializerSession session) {
		if (!(serializedValue instanceof SerializedObject) || session.facades(object)) {
			return;
		}
		SerializedObject serializedObject = (SerializedObject) serializedValue;
		Class<?> objectClass = object.getClass();
		while (objectClass != Object.class && !session.excludes(objectClass)) {
			for (Field f : objectClass.getDeclaredFields()) {
				if (!session.excludes(f)) {
					serializedObject.addField(resolvedFieldOf(session, object, f));
				}
			}
			objectClass = objectClass.getSuperclass();
		}
	}

}
