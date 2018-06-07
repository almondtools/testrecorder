package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.allFields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectedFieldsComparisonStrategy implements ComparisonStrategy {

	private Set<String> fields;
	
	public SelectedFieldsComparisonStrategy(Set<String> fields) {
		this.fields = fields;
	}
	
	public static SelectedFieldsComparisonStrategy comparingFields(Collection<String> fields) {
		return new SelectedFieldsComparisonStrategy(new HashSet<>(fields));
	}
	
	public static SelectedFieldsComparisonStrategy comparingFields(String... fields) {
		return comparingFields(asList(fields));
	}

	@Override
	public List<GenericComparison> extend(GenericComparison comparison) throws ComparisonException {
		List<GenericComparison> todo = new ArrayList<>();
		Class<?> clazz = comparison.requireSameClass();
		for (Field field : allFields(clazz)) {
			String fieldName = field.getName();
			if (!fields.contains(fieldName)) {
				continue;
			}
			todo.add(comparison.newComparison(fieldName));
		}
		return todo;
	}

}
