package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.util.Types.allFields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DefaultComparisonStrategy implements ComparisonStrategy {

	public DefaultComparisonStrategy() {
	}
	
	public static DefaultComparisonStrategy all() {
		return new DefaultComparisonStrategy();
	}
	
	@Override
	public List<GenericComparison> extend(GenericComparison comparison) {
		List<GenericComparison> todo = new ArrayList<>();
		Class<?> clazz = comparison.getLeft() != null
			? comparison.getLeft().getClass()
			: comparison.getRight().getClass();
		for (Field field : allFields(clazz)) {
			String fieldName = field.getName();
			todo.add(comparison.newComparison(fieldName));
		}
		return todo;
	}

}
