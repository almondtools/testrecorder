package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedNull is only valid and strongly recommended for any value that is null. Use the factory method  
 * {@link #nullInstance(Type)}
 */
public class SerializedNull extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private static final Map<Type, SerializedNull> KNOWN_LITERALS = new HashMap<>();
	public static final SerializedNull VOID = nullInstance(void.class);

	private SerializedNull(Type type) {
		super(type);
	}
	
	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	public static SerializedNull nullInstance(Type type) {
		return KNOWN_LITERALS.computeIfAbsent(type, typ -> new SerializedNull(typ));
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
