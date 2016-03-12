package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class ExcludeGenerated implements Predicate<Field> {

	@Override
	public boolean test(Field field) {
		return field.getName().indexOf('$') >= 0;
	}

}
