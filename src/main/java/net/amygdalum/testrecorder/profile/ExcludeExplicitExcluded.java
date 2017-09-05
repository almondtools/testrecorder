package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;

import net.amygdalum.testrecorder.FieldsAtRuntime;
import net.amygdalum.testrecorder.SerializationProfile.Excluded;

public class ExcludeExplicitExcluded implements FieldsAtRuntime {

	@Override
	public boolean matches(Field field) {
		return field.getAnnotation(Excluded.class) != null;
	}

}
