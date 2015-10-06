package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedNull implements SerializedValue {

	private static final Map<Type, SerializedNull> KNOWN_LITERALS = new HashMap<>();
	
	private Type type;
	
	public SerializedNull(Type type) {
		this.type = type;
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitNull(this);
	}

	public static SerializedNull nullInstance(Type type) {
		return KNOWN_LITERALS.computeIfAbsent(type, typ -> new SerializedNull(typ));
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

	@Override
	public int hashCode() {
		return type.getTypeName().hashCode() * 7 + 29;
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
		SerializedNull that = (SerializedNull) obj;
		return this.type == that.type;
	}
	
}
