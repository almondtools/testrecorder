package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.util.Types.baseType;

import java.io.Serializable;
import java.lang.reflect.Type;

public class SerializedArgument implements Comparable<SerializedArgument>, Serializable, SerializedRole {

    private int index;
	private MethodSignature signature;
    private SerializedValue value;

    public SerializedArgument(int index, MethodSignature signature, SerializedValue value) {
    	assert signature != null;
    	assert value != null;
    	this.index = index;
        this.signature = signature;
        this.value = value;
    }

    public int getIndex() {
		return index;
	}
    
    public MethodSignature getSignature() {
		return signature;
	}

	public Type getType() {
		return signature.argumentTypes[index];
	}

	public SerializedValue getValue() {
        return value;
    }

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
        return visitor.visitArgument(this);
    }

    public String toString() {
		return "(" + signature.argumentTypes[index].getTypeName() + " " + baseType(signature.argumentTypes[index]).getSimpleName().toLowerCase() + index + ": " + value.toString() + ")";
	}

    public int hashCode() {
        return index * 37
            + signature.hashCode() * 17
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
            && this.signature.equals(that.signature)
            && this.value.equals(that.value);
    }
    
    public int compareTo(SerializedArgument o) {
        return Integer.compare(index, o.index);
    }

}
