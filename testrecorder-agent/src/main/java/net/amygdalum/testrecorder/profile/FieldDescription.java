package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;

import org.objectweb.asm.Type;

public class FieldDescription implements Fields {

	private String className;
	private String fieldName;
	private String fieldDescriptor;

	public FieldDescription(String className, String fieldName, String fieldDescriptor) {
		this.className = className;
		this.fieldName = fieldName;
		this.fieldDescriptor = fieldDescriptor;
	}

	@Override
	public boolean matches(Field field) {
		String className = Type.getInternalName(field.getDeclaringClass());
		String fieldName = field.getName();
		String fieldDescriptor = Type.getDescriptor(field.getType());
		return this.className.equals(className)
			&& this.fieldName.equals(fieldName)
			&& this.fieldDescriptor.equals(fieldDescriptor);
	}

	@Override
	public boolean matches(String className, String fieldName, String fieldDescriptor) {
		return this.className.equals(className)
			&& this.fieldName.equals(fieldName)
			&& this.fieldDescriptor.equals(fieldDescriptor);
	}

}
