package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A serializable variant of WildcardType.
 * 
 * Do not use this type directly (otherwise serializability may be not ensured). Use {@link Types#wildcard(Type[], Type[])}
 */
public final class SerializableWildcardType implements WildcardType, Serializable {

	private Type[] upperBounds;
	private Type[] lowerBounds;

	public SerializableWildcardType(Type[] upperBounds, Type[] lowerBounds) {
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
	}

	@Override
	public Type[] getUpperBounds() {
		return upperBounds;
	}

	@Override
	public Type[] getLowerBounds() {
		return lowerBounds;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(upperBounds) * 5
			+ Arrays.hashCode(lowerBounds) * 7
			+ 23;
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
		SerializableWildcardType that = (SerializableWildcardType) obj;
		return Arrays.equals(this.upperBounds, that.upperBounds)
			&& Arrays.equals(this.lowerBounds, that.lowerBounds);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("?");
		String lowerBoundsStr = Stream.of(lowerBounds)
			.map(type -> type.getTypeName())
			.collect(joining(", "));
		if (!lowerBoundsStr.isEmpty()) {
			buffer.append(" super ").append(lowerBoundsStr);
		}
		String upperBoundsStr = Stream.of(upperBounds)
			.filter(type -> type != Object.class)
			.map(type -> type.getTypeName())
			.collect(joining(", "));
		if (!upperBoundsStr.isEmpty()) {
			buffer.append(" extends ").append(upperBoundsStr);
		}
		return buffer.toString();
	}

}