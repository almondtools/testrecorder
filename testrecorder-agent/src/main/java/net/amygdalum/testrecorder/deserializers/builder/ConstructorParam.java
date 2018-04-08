package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.util.Types.equalGenericTypes;
import static net.amygdalum.testrecorder.util.Types.isLiteral;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.runtime.DefaultValue;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedField;

public class ConstructorParam {

    private Constructor<?> constructor;
    private int paramNumber;
    private SerializedField field;
    private Object value;
    private Type type;
    private boolean needsCast;

    public ConstructorParam(Constructor<?> constructor, int paramNumber, SerializedField field, Object value) {
        this.constructor = constructor;
        this.paramNumber = paramNumber;
        this.field = field;
        this.value = value;
    }

    public ConstructorParam(Constructor<?> constructor, int paramNumber) {
        this.constructor = constructor;
        this.paramNumber = paramNumber;
    }

    public int getParamNumber() {
        return paramNumber;
    }

    public SerializedField getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public ConstructorParam insertTypeCasts() {
        this.needsCast = true;
        return this;
    }

    @Override
    public String toString() {
        return constructor.toString() + ":" + paramNumber + "=" + field.getValue() + "=> " + field.getName();
    }

    public SerializedValue computeSerializedValue() {
        Object value = this.value != null ? this.value : DefaultValue.of(type);
        if (field == null) {
            if (type == String.class) {
                return nullInstance(String.class);
            } else if (isLiteral(type)) {
                return literal(type, value);
            } else {
                return nullInstance(type);
            }
        } else {
            return field.getValue();
        }
    }

    public ConstructorParam assertType(Type type) {
        this.type = type;
        return this;
    }

    private boolean castNeeded() {
        if (needsCast) {
            return true;
        }
        if (field == null
            || value == null
            || type == null) {
            return false;
        }
        return !equalGenericTypes(type,field.getType());
    }

    public Computation compile(TypeManager types, SetupGenerators generator, DeserializerContext context) {
        SerializedValue serializedValue = computeSerializedValue();
        Computation computation = serializedValue.accept(generator, context);
        String value = computation.getValue();
        boolean stored = computation.isStored();
        
        if (context.needsAdaptation(type, computation.getType()))  {
            value = context.adapt(value, type, computation.getType());
            stored = true;
        } else if (castNeeded()) {
            value = cast(types.getVariableTypeName(type), value);
            stored = true;
        }
        computation = new Computation(value, type, stored, computation.getStatements());

        return computation;
    }

}
