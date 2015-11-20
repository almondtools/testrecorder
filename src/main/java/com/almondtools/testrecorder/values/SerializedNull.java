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
	
	private SerializedNull(Type type) {
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

}
