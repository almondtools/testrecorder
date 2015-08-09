package com.almondtools.iit.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.Serializer;
import com.almondtools.iit.SerializerFacade;
import com.almondtools.iit.SnapshotExcluded;
import com.almondtools.iit.values.SerializedObject;

public class GenericSerializer implements Serializer {

	private SerializerFacade facade;
	private Class<?> clazz;

	public GenericSerializer(SerializerFacade facade, Class<?> clazz) {
		this.facade = facade;
		this.clazz = clazz;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedValue generate() {
		return new SerializedObject(clazz);
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		SerializedObject newSerializedObject = (SerializedObject) serializedObject;
		Class<?> objectClass = object.getClass();
		while (objectClass != Object.class) {
			for (Field f : objectClass.getDeclaredFields()) {
				if (!f.isAnnotationPresent(SnapshotExcluded.class)) {
					newSerializedObject.addField(facade.serialize(f, object));
				}
			}
			objectClass = objectClass.getSuperclass();
		}
	}

}
