package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.extensionpoint.ExtensionStrategy.EXTENDING;

import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;

@ExtensionPoint(strategy=EXTENDING)
public interface Serializer<T extends SerializedValue> {

	List<Class<?>> getMatchingClasses();
	
	/**
	 * returns all non primitive components of the object.
	 * 
	 * Only objects that are returned in this method may be found in 
	 * {@link #populate(SerializedValue, Object, SerializerSession)} later on.
	 */
	Stream<?> components(Object object, SerializerSession session);

	/**
	 * returns a new instance of serialized value.
	 * 
	 * It is called early in serialization process, later serving as placeholder and object to populate
	 */
	T generate(Class<?> type, SerializerSession session);

	/**
	 * populate fills serializedValue with its internal components.
	 * 
	 * Note to declare all components in {@link #components(Object, SerializerSession)}, otherwise they
	 * will not be found in the session. 
	 */
	void populate(T serializedObject, Object object, SerializerSession session);

}
