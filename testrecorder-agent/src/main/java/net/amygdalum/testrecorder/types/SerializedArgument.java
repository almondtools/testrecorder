package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.util.Types.baseType;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class SerializedArgument implements Comparable<SerializedArgument>, Serializable, SerializedRole {

    private int index;
	private Type type;
    private Annotation[] annotations;
    private SerializedValue value;

    public SerializedArgument(int index, Type type, Annotation[] annotations, SerializedValue value) {
    	assert type instanceof Serializable;
    	assert annotations != null;
    	assert value != null;
    	this.index = index;
        this.type = type;
        this.annotations = annotations;
        this.value = value;
    }

    public int getIndex() {
		return index;
	}

	public Type getType() {
        return type;
    }

	public SerializedValue getValue() {
        return value;
    }

	@Override
	public Annotation[] getAnnotations() {
        return annotations;
    }

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
        return visitor.visitArgument(this);
    }

    public String toString() {
		return "(" + type.getTypeName() + " " + baseType(type).getSimpleName().toLowerCase() + index + ": " + value.toString() + ")";
	}

    public int hashCode() {
        return index * 37
            + type.getTypeName().hashCode() * 17
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
        SerializedArgument that = (SerializedArgument) obj;
        return this.index == that.index
            && this.type == that.type
            && this.value.equals(that.value);
    }
    
    public int compareTo(SerializedArgument o) {
        return Integer.compare(index, o.index);
    }
}
