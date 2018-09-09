package net.amygdalum.testrecorder.types;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

import net.amygdalum.testrecorder.util.Types;

public class SerializedField implements Comparable<SerializedField>, Serializable, SerializedRole {

    private String name;
    private Type type;
    private SerializedValue value;
    private Class<?> clazz;

    public SerializedField(Class<?> clazz, String name, Type type, SerializedValue value) {
    	assert name != null;
    	assert type instanceof Serializable;
    	assert value != null;
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

	@Override
	public Annotation[] getAnnotations() {
        try {
            return Types.getDeclaredField(clazz, name).getAnnotations();
        } catch (NoSuchFieldException e) {
            return new Annotation[0];
        }
    }

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
        return visitor.visitField(this);
    }

    public String toString() {
		return type.getTypeName() + " " + name + ": " + value.toString();
    }

    public int hashCode() {
        return name.hashCode() * 31
            + type.getTypeName().hashCode() * 13
            + value.hashCode();
    }

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
        return Objects.equals(this.clazz,that.clazz)
            && this.name.equals(that.name)
            && this.type == that.type
            && this.value.equals(that.value);
    }
    
    public int compareTo(SerializedField o) {
        return getName().compareTo(o.getName());
    }
}
