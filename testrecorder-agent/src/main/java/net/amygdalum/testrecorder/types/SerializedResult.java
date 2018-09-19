package net.amygdalum.testrecorder.types;

import java.io.Serializable;
import java.lang.reflect.Type;

public class SerializedResult implements Serializable, SerializedRole {

	private MethodSignature signature;
	private SerializedValue value;

	public SerializedResult(MethodSignature signature, SerializedValue value) {
		assert signature != null;
		assert value != null;
		this.signature = signature;
		this.value = value;
	}

	public MethodSignature getSignature() {
		return signature;
	}
	
	public Type getType() {
		return signature.resultType;
	}
	
	public SerializedValue getValue() {
		return value;
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitResult(this);
	}

	public String toString() {
		return "=>" + signature.resultType.getTypeName() + ": " + value.toString();
	}

	public int hashCode() {
		return signature.hashCode() * 13
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
		SerializedResult that = (SerializedResult) obj;
		return this.signature.equals(that.signature)
			&& this.value.equals(that.value);
	}

}
