package com.almondtools.invitroderivatives.values;

import java.util.HashMap;
import java.util.Map;

import com.almondtools.invitroderivatives.SerializedValue;
import com.almondtools.invitroderivatives.SerializedValueVisitor;
import com.almondtools.invitroderivatives.visitors.SerializedValuePrinter;

public class SerializedLiteral implements SerializedValue {

	private static final Map<Object, SerializedLiteral> KNOWN_LITERALS = new HashMap<>();
	
	private Class<?> type;
	private Object value;

	public SerializedLiteral(Class<?> type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public static SerializedLiteral of(Class<?> type, Object value) {
		return KNOWN_LITERALS.computeIfAbsent(value, val -> new SerializedLiteral(type, val));
	}

	@Override
	public Class<?> getType() {
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
		return type.getName().hashCode() * 19
			+ value.hashCode();
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
			&& this.value.equals(that.value);
	}
	
}
