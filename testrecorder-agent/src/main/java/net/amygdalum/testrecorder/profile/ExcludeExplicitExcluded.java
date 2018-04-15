package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;

public class ExcludeExplicitExcluded implements FieldsAtRuntime {

	@Override
	public boolean matches(Field field) {
		return field.getAnnotation(Excluded.class) != null;
	}

}
