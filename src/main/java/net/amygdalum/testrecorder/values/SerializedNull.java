package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.TypeManager.getBase;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedNull is only valid and strongly recommended for any value that is null. Use the factory method  
 * {@link #nullInstance(Type)}
 */
public class SerializedNull implements SerializedReferenceType {

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
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public Class<?> getValueType() {
		return getBase(type);
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
	}

	public static SerializedNull nullInstance(Type type) {
		return KNOWN_LITERALS.computeIfAbsent(type, typ -> new SerializedNull(typ));
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
