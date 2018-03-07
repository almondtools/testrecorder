package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.objectweb.asm.Type;

public class FieldsByName implements Fields {

	private static final Pattern NAME = Pattern.compile("[\\w$.]+");

	private String clazz;
	private String name;

	public FieldsByName(String name) {
		if (!NAME.matcher(name).matches()) {
			throw new IllegalArgumentException("field name should contain only word characters, dot and $, but was: " + name);
		}
		int lastDot = name.lastIndexOf('.');
		if (lastDot > -1) {
			this.clazz = name.substring(0, lastDot);
			this.name = name.substring(lastDot + 1);
		} else {
			this.name = name;
		}
	}

	@Override
	public boolean matches(Field field) {
		if (clazz == null) {
			return field.getName().equals(name);
		}
		return field.getName().equals(name)
			&& field.getDeclaringClass().getName().equals(clazz);
	}

	@Override
	public boolean matches(String className, String fieldName, String fieldDescriptor) {
		if (clazz == null) {
			return fieldName.equals(name);
		}
		String refName = Type.getObjectType(className).getClassName();
		return fieldName.equals(name)
			&& refName.equals(clazz);
	}

}
