package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SnapshotExcluded;
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
	public SerializedReferenceType generate(Type resultType, Type type) {
		if (facade.excludes(baseType(type))) {
			return SerializedNull.nullInstance(type);
		} else {
			return new SerializedObject(type).withResult(resultType);
		}
	}

	@Override
	public void populate(SerializedReferenceType serializedValue, Object object) {
		if (!(serializedValue instanceof SerializedObject)) {
			return;
		}
		SerializedObject serializedObject = (SerializedObject) serializedValue;
		Class<?> objectClass = object.getClass();
		while (objectClass != Object.class && !facade.excludes(objectClass)) {
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
