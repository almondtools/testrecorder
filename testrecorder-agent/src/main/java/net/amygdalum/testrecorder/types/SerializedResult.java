package net.amygdalum.testrecorder.types;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class SerializedResult implements Serializable, SerializedRole {

	private Type type;
	private Annotation[] annotations;
	private SerializedValue value;

	public SerializedResult(Type type, Annotation[] annotations, SerializedValue value) {
		assert type instanceof Serializable;
		assert annotations != null;
		assert value != null;
		this.type = type;
		this.annotations = annotations;
		this.value = value;
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
		return visitor.visitResult(this);
	}

	public String toString() {
		return "=>" + type.getTypeName() + ": " + value.toString();
	}

	public int hashCode() {
		return type.getTypeName().hashCode() * 13
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
		return this.type == that.type
			&& this.value.equals(that.value);
	}

}
