package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.util.Types.getDeclaredField;
import static net.amygdalum.testrecorder.util.Types.serializableOf;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldSignature implements Serializable {

	public Class<?> declaringClass;
	public Type type;
	public String fieldName;

	public FieldSignature(Class<?> declaringClass, Type type, String fieldName) {
		assert declaringClass != null;
		assert fieldName != null;
		assert type != null;
		this.declaringClass = declaringClass;
		this.type = serializableOf(type);
		this.fieldName = fieldName;
	}

	public Field resolveField() throws NoSuchFieldException {
		return getDeclaredField(declaringClass, fieldName);
	}

	@Override
	public int hashCode() {
		return declaringClass.hashCode() * 29
			+ fieldName.hashCode() * 17
			+ type.hashCode() * 11;
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
		FieldSignature that = (FieldSignature) obj;
		return this.declaringClass.equals(that.declaringClass)
			&& this.fieldName.equals(that.fieldName)
			&& this.type.equals(that.type);
	}

	@Override
	public String toString() {
		return type.getTypeName() + " " + fieldName + " of " + declaringClass.getName();
	}

}
