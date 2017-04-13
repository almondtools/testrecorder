package net.amygdalum.testrecorder.profile;

import static net.amygdalum.testrecorder.util.Types.isStatic;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class ExcludeStatic implements Predicate<Field> {

	@Override
	public boolean test(Field field) {
		return isStatic(field);
	}

}
