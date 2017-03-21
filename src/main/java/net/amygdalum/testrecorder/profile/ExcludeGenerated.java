package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class ExcludeGenerated implements Predicate<Field> {

	@Override
	public boolean test(Field field) {
	    if (field.getName().startsWith("this$")) {
	        // anonymous/nested classes use this$ to reference their outer class
	        return false;
	    }
		return field.isSynthetic()
		    || field.getName().indexOf('$') >= 0;
	}

}
