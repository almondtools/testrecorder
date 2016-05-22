package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedLiteral is only valid for primitive types and non-null Strings. For this use the factory method 
 * {@link #literal(Type, Object)}
 */
public class SerializedLiteral extends AbstractSerializedValue implements SerializedValueType {

	public static Set<Class<?>> LITERAL_TYPES = new HashSet<>(Arrays.asList(
		boolean.class, char.class, byte.class, short.class, int.class, float.class, long.class, double.class,
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class,
		String.class));

	private static final Map<Object, SerializedLiteral> KNOWN_LITERALS = new HashMap<>();

	private Type resultType;
	private Object value;

	private SerializedLiteral(Type type, Object value) {
		super(value.getClass());
		this.resultType = type;
		this.value = value;
	}

	public static boolean isLiteral(Type type) {
		return LITERAL_TYPES.contains(type);
	}

	public static SerializedLiteral literal(Object value) {
		return literal(value.getClass(), value);
	}
	
	public static SerializedLiteral literal(Type type, Object value) {
		return KNOWN_LITERALS.computeIfAbsent(value, val -> new SerializedLiteral(type, val));
	}

	@Override
	public Type getResultType() {
		return resultType;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitValueType(this);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
