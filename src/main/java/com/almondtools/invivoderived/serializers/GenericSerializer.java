package com.almondtools.invivoderived.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.SnapshotExcluded;
import com.almondtools.invivoderived.values.SerializedObject;

public class GenericSerializer implements Serializer<SerializedObject> {

	private SerializerFacade facade;

	public GenericSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedObject generate(Type type) {
		return new SerializedObject(type);
	}

	@Override
	public void populate(SerializedObject serializedObject, Object object) {
		serializedObject.setObjectType(object.getClass());
		Class<?> objectClass = object.getClass();
		while (objectClass != Object.class) {
			for (Field f : objectClass.getDeclaredFields()) {
				if (isSerializable(f)) {
					serializedObject.addField(facade.serialize(f, object));
				}
			}
			objectClass = objectClass.getSuperclass();
		}
	}

	private boolean isSerializable(Field field) {
		return !field.isAnnotationPresent(SnapshotExcluded.class)
			&& !field.isSynthetic()
			&& !facade.excludes(field);
	}

}
