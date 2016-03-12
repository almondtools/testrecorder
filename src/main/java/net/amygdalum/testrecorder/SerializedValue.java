package net.amygdalum.testrecorder;

import java.lang.reflect.Type;

/**
 * A serialized value.
 * 
 * A serialized value does reflect the relations between the real objects. As a consequence it must hold
 * - that if two objects are the same, their serialized value must also be the same
 * - that if two serialized values are the same, their original value is the same
 * 
 * The hascode method should reflect the identity of the serialized or nonserialized value (identity hash code should be fine)
 * The equals method of a serialized value must return true if both values are the same (==) and false otherwise (default equal should be fine)
 *
 */
public interface SerializedValue {

	<T> T accept(SerializedValueVisitor<T> visitor);

	Type getType();

	Class<?> getValueType();

}
