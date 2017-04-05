package net.amygdalum.testrecorder.values;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;
import net.amygdalum.testrecorder.util.Types;

public class SerializedField implements Comparable<SerializedField> {

    private String name;
    private Type type;
    private SerializedValue value;
    private Class<?> clazz;

    public SerializedField(Class<?> clazz, String name, Type type, SerializedValue value) {
        this.clazz = clazz;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Class<?> getDeclaringClass() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public SerializedValue getValue() {
        return value;
    }

    public Annotation[] getAnnotations() {
        try {
            return Types.getDeclaredField(clazz, name).getAnnotations();
        } catch (NoSuchFieldException e) {
            return new Annotation[0];
        }
    }

    public <T> T accept(Deserializer<T> visitor) {
        return accept(visitor, DeserializerContext.NULL);
    }

    public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
        return visitor.visitField(this, context);
    }

    @Override
    public String toString() {
        return accept(new ValuePrinter());
    }

    @Override
    public int compareTo(SerializedField o) {
        return name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31
            + type.getTypeName().hashCode() * 13
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
        SerializedField that = (SerializedField) obj;
        return this.clazz.equals(that.clazz)
            && this.name.equals(that.name)
            && this.type == that.type
            && this.value.equals(that.value);
    }

}
