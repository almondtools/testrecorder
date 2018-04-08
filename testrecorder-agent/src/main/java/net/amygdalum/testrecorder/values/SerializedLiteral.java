package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;

/**
 * Serializing to SerializedLiteral is only valid for primitive types and non-null Strings. For this use the factory method 
 * {@link #literal(Type, Object)}
 */
public class SerializedLiteral extends AbstractSerializedValue implements SerializedValueType {

    private static final Map<Object, SerializedLiteral> KNOWN_PRIMITIVE_LITERALS = new HashMap<>();
    private static final Map<Object, SerializedLiteral> KNOWN_LITERALS = new HashMap<>();

    private Type resultType;
    private Object value;

    private SerializedLiteral(Type type, Object value) {
        super(value.getClass());
        this.resultType = type;
        this.value = value;
    }

    public static SerializedLiteral literal(Object value) {
        return literal(value.getClass(), value);
    }

    public static SerializedLiteral literal(Type type, Object value) {
        if (baseType(type).isPrimitive()) {
            return KNOWN_PRIMITIVE_LITERALS.computeIfAbsent(value, val -> new SerializedLiteral(type, val));
        } else {
            return KNOWN_LITERALS.computeIfAbsent(value, val -> new SerializedLiteral(type, val));
        }
    }

    @Override
    public Type[] getUsedTypes() {
        return new Type[] {resultType};
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
    public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
        return visitor.visitValueType(this, context);
    }

    @Override
    public String toString() {
		return ValuePrinter.print(this);
    }

}
