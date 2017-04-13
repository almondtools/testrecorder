package net.amygdalum.testrecorder.profile;

import static net.amygdalum.testrecorder.util.Types.isUnhandledSynthetic;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class ExcludeGenerated implements Predicate<Field> {

	@Override
	public boolean test(Field field) {
	    return isUnhandledSynthetic(field);
	}

}
