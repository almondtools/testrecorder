package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import net.amygdalum.testrecorder.SnapshotExcluded;

public class ExcludeExplicitExcluded implements Predicate<Field> {

	@Override
	public boolean test(Field field) {
		return field.getAnnotation(SnapshotExcluded.class) != null;
	}

}
