package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedNull is only valid and strongly recommended for any value that is null. Use the factory method  
 * {@link #nullInstance(Type)}
 */
public class SerializedNull extends AbstractSerializedReferenceType implements SerializedReferenceType {

	private static final Map<Type, SerializedNull> KNOWN_LITERALS = new HashMap<>();

	private SerializedNull(Type type) {
		super(type);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
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
