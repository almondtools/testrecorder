package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.visitors.TypeManager.getBase;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.visitors.SerializedValuePrinter;

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
	public Class<?> getValueType() {
		return getBase(type);
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
