package net.amygdalum.testrecorder.profile;

import static net.amygdalum.testrecorder.util.Types.isUnhandledSynthetic;

import java.lang.reflect.Field;

import net.amygdalum.testrecorder.FieldsAtRuntime;

public class ExcludeGenerated implements FieldsAtRuntime {

	@Override
	public boolean matches(Field field) {
	    return isUnhandledSynthetic(field);
	}

}
