package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Field;
import java.util.List;

import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class GenericSerializer implements Serializer<SerializedReferenceType> {

	private SerializerFacade facade;

	public GenericSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedReferenceType generate(Class<?> type, SerializerSession session) {
		if (session.excludes(baseType(type))) {
			return SerializedNull.nullInstance(type);
		} else {
			return new SerializedObject(type);
		}
	}

	@Override
	public void populate(SerializedReferenceType serializedValue, Object object, SerializerSession session) {
		if (!(serializedValue instanceof SerializedObject)) {
			return;
		}
		SerializedObject serializedObject = (SerializedObject) serializedValue;
		if (!session.facades(object)) {
			Class<?> objectClass = object.getClass();
			while (objectClass != Object.class && !session.excludes(objectClass)) {
				for (Field f : objectClass.getDeclaredFields()) {
					if (!session.excludes(f)) {
						serializedObject.addField(facade.serialize(f, object, session));
					}
				}
				objectClass = objectClass.getSuperclass();
			}
		}
	}

}
