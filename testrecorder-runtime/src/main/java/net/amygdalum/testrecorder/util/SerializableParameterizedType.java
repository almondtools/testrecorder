package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A serializable variant of ParameterizedType.
 * 
 * Do not use this type directly (otherwise serializability may be not ensured). Use {@link Types#parameterized(Type, Type, Type[])}
 */
public final class SerializableParameterizedType implements ParameterizedType, Serializable {

	private Type raw;
	private Type owner;
	private Type[] typeArgs;

	public SerializableParameterizedType(Type raw, Type owner, Type... typeArgs) {
		this.raw = raw;
		this.owner = owner;
		this.typeArgs = typeArgs;
	}

	@Override
	public Type getRawType() {
		return raw;
	}

	@Override
	public Type getOwnerType() {
		return owner;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return typeArgs;
	}

	@Override
	public int hashCode() {
		return raw.hashCode() * 3 + (owner == null ? 0 : owner.hashCode() * 5) + Arrays.hashCode(typeArgs) * 7 + 13;
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
		SerializableParameterizedType that = (SerializableParameterizedType) obj;
		return this.raw.equals(that.raw)
			&& Objects.equals(this.owner, that.owner)
			&& Arrays.equals(this.typeArgs, that.typeArgs);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(raw.getTypeName());
		buffer.append('<');
		if (typeArgs != null && typeArgs.length > 0) {
			buffer.append(Stream.of(typeArgs)
				.map(type -> type.getTypeName())
				.collect(joining(", ")));
		}
		buffer.append('>');
		return buffer.toString();
	}

}