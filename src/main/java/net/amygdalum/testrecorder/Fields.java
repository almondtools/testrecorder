package net.amygdalum.testrecorder;

import java.lang.reflect.Field;

import net.amygdalum.testrecorder.profile.FieldDescription;
import net.amygdalum.testrecorder.profile.FieldsByName;

public interface Fields {

	boolean matches(Field field);

	boolean matches(String className, String fieldName, String fieldDescriptor);

	static Fields byName(String name) {
		return new FieldsByName(name);
	}

	static Fields byDescription(String className, String fieldName, String fieldDescriptor) {
		return new FieldDescription(className, fieldName, fieldDescriptor);
	}

}
