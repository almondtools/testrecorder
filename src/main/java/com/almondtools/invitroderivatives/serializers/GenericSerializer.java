package com.almondtools.invitroderivatives.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;

import com.almondtools.invitroderivatives.SerializedValue;
import com.almondtools.invitroderivatives.Serializer;
import com.almondtools.invitroderivatives.SerializerFacade;
import com.almondtools.invitroderivatives.analyzer.SnapshotExcluded;
import com.almondtools.invitroderivatives.values.SerializedObject;

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
				if (isSerializable(f)) {
					newSerializedObject.addField(facade.serialize(f, object));
				}
			}
			objectClass = objectClass.getSuperclass();
		}
	}

	private boolean isSerializable(Field field) {
		return !field.isAnnotationPresent(SnapshotExcluded.class)
			&& field.getName().charAt(0) != '$';
	}

}
