package net.amygdalum.testrecorder.types;

import java.io.Serializable;

public class SerializedKeyValue implements Serializable, SerializedRole {

	private SerializedValue key;
	private SerializedValue value;

	public SerializedKeyValue(SerializedValue key, SerializedValue value) {
		this.key = key;
		this.value = value;
	}

	public SerializedValue getKey() {
		return key;
	}

	public SerializedValue getValue() {
		return value;
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
        return visitor.visitKeyValue(this);
    }

	@Override
	public String toString() {
		return key.toString() + ":" + value.toString();
	}

	public int hashCode() {
		return key.hashCode() * 13
			+ value.hashCode() * 17;
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
		SerializedKeyValue that = (SerializedKeyValue) obj;
		return this.key.equals(that.key)
			&& this.value.equals(that.value);
	}

}
