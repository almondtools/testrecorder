package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedLiteral implements SerializedValue {

	public static Set<Class<?>> LITERAL_TYPES = new HashSet<>(Arrays.asList(
		boolean.class, char.class, byte.class, short.class, int.class, float.class, long.class, double.class,
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class,
		String.class));

	private static final Map<Object, SerializedLiteral> KNOWN_LITERALS = new HashMap<>();

	private Type type;
	private Object value;

	public SerializedLiteral(Type type, Object value) {
		this.type = type;
		this.value = value;
	}

	public static boolean isLiteral(Type type) {
		return LITERAL_TYPES.contains(type);
	}

	public static SerializedLiteral literal(Type type, Object value) {
		return KNOWN_LITERALS.computeIfAbsent(value, val -> new SerializedLiteral(type, val));
	}

	@Override
	public Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitLiteral(this);
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

	@Override
	public int hashCode() {
		return type.getTypeName().hashCode() * 19
			+ (value == null ? 0 : value.hashCode());
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
		SerializedLiteral that = (SerializedLiteral) obj;
		return this.type == that.type
			&& (this.value == null ? that.value == null : this.value.equals(that.value));
	}

}
