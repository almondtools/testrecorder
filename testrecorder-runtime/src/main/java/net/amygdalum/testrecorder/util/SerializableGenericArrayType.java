package net.amygdalum.testrecorder.util;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * A serializable variant of GenericArrayType.
 * 
 * Do not use this type directly (otherwise serializability may be not ensured). Use {@link Types#genericArray(Type)}
 */
public final class SerializableGenericArrayType implements GenericArrayType, Serializable {

	private Type componentType;

	public SerializableGenericArrayType(Type componentType) {
		this.componentType = componentType;
	}

	@Override
	public Type getGenericComponentType() {
		return componentType;
	}

	@Override
	public int hashCode() {
		return componentType.hashCode() + 19;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SerializableGenericArrayType that = (SerializableGenericArrayType) obj;
		return this.componentType.equals(that.componentType);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(componentType.getTypeName());
		buffer.append("[]");
		return buffer.toString();
	}

}