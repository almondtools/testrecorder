package com.almondtools.invivoderived.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.analyzer.SnapshotExcluded;
import com.almondtools.invivoderived.values.SerializedObject;

public class GenericSerializer implements Serializer {

	private SerializerFacade facade;

	public GenericSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedValue generate(Type type) {
		return new SerializedObject(type);
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		SerializedObject newSerializedObject = (SerializedObject) serializedObject;
		newSerializedObject.setObjectType(object.getClass());
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
			&& field.getName().charAt(0) != '$'
			&& ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
			&& !facade.excludes(field.getType());
	}

}
