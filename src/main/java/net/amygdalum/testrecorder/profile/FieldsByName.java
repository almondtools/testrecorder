package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;

public class FieldsByName implements Fields {

	private String name;

	public FieldsByName(String name) {
		this.name = name;
	}

	@Override
	public boolean matches(Field field) {
		return field.getName().equals(name);
	}

	@Override
	public boolean matches(String className, String fieldName, String fieldDescriptor) {
		return fieldName.equals(name);
	}

}
