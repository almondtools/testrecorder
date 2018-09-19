package net.amygdalum.testrecorder.types;

import java.io.Serializable;
import java.lang.reflect.Type;

public class SerializedField implements Comparable<SerializedField>, Serializable, SerializedRole {

    private FieldSignature signature;
    private SerializedValue value;

    public SerializedField(FieldSignature signature, SerializedValue value) {
    	assert signature != null;
    	assert value != null;
    	this.signature = signature;
        this.value = value;
    }
    
    public FieldSignature getSignature() {
		return signature;
	}

    public Class<?> getDeclaringClass() {
        return signature.declaringClass;
    }

    public String getName() {
        return signature.fieldName;
    }

	public Type getType() {
        return signature.type;
    }

	public SerializedValue getValue() {
        return value;
    }

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
        return visitor.visitField(this);
    }

    public String toString() {
		return signature.type.getTypeName() + " " + signature.fieldName + ": " + value.toString();
    }

    public int hashCode() {
        return signature.hashCode() * 31
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
        return this.signature.equals(that.signature)
            && this.value.equals(that.value);
    }
    
    public int compareTo(SerializedField o) {
        return getName().compareTo(o.getName());
    }
}
