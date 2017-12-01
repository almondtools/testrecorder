package net.amygdalum.testrecorder.values;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Types;

public class SerializedField implements SerializedFieldType {

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

    @Override
	public Class<?> getDeclaringClass() {
        return clazz;
    }

    /* (non-Javadoc)
	 * @see net.amygdalum.testrecorder.values.SerializedFieldType#getName()
	 */
    @Override
	public String getName() {
        return name;
    }

    @Override
	public Type getType() {
        return type;
    }

    @Override
	public SerializedValue getValue() {
        return value;
    }

    @Override
	public Annotation[] getAnnotations() {
        try {
            return Types.getDeclaredField(clazz, name).getAnnotations();
        } catch (NoSuchFieldException e) {
            return new Annotation[0];
        }
    }

    @Override
	public <T extends Annotation> Optional<T> getAnnotation(Class<T> clazz) {
        Annotation[] annotations = getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (clazz.isInstance(annotations[i])) {
                return Optional.of(clazz.cast(annotations[i]));
            }
        }
        return Optional.empty();
    }

    @Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
        return visitor.visitField(this, context);
    }

    @Override
    public String toString() {
		return ValuePrinter.print(this);
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
