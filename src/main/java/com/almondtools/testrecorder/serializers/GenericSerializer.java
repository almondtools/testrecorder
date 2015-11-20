package com.almondtools.testrecorder.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import com.almondtools.testrecorder.Serializer;
import com.almondtools.testrecorder.SerializerFacade;
import com.almondtools.testrecorder.SnapshotExcluded;
import com.almondtools.testrecorder.values.SerializedObject;

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
	public SerializedObject generate(Type type, Class<?> valueType) {
		return new SerializedObject(type, valueType);
	}

	@Override
	public void populate(SerializedObject serializedObject, Object object) {
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
