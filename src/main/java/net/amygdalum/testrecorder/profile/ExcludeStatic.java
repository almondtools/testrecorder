package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class ExcludeStatic implements Predicate<Field> {

	@Override
	public boolean test(Field field) {
		return (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
	}

}
