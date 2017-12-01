package net.amygdalum.testrecorder.profile;

import static net.amygdalum.testrecorder.util.Types.isStatic;

import java.lang.reflect.Field;

public class ExcludeStatic implements FieldsAtRuntime {

	@Override
	public boolean matches(Field field) {
		return isStatic(field);
	}

}
