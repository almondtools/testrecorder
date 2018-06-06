package net.amygdalum.testrecorder.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListEnabledComparisonStrategy implements ComparisonStrategy {

	private ComparisonStrategy base;

	public ListEnabledComparisonStrategy(ComparisonStrategy base) {
		this.base = base;
	}

	public static ComparisonStrategy extendByLists(ComparisonStrategy base) {
		return new ListEnabledComparisonStrategy(base);
	}

	@Override
	public List<GenericComparison> extend(GenericComparison comparison) throws ComparisonException {
		List<GenericComparison> todo = new ArrayList<>();
		Class<?> clazz = comparison.getLeft() != null
			? comparison.getLeft().getClass()
			: comparison.getRight().getClass();
		if (List.class.isAssignableFrom(clazz)) {
			List<?> left = (List<?>) comparison.getLeft();
			List<?> right = (List<?>) comparison.getRight();
			if (left.size() != right.size()) {
				throw new ComparisonException();
			}
			Iterator<?> li = left.iterator();
			Iterator<?> ri = right.iterator();
			int index = 0;
			while (li.hasNext() && ri.hasNext()) {
				Object leftItem = li.next();
				Object rightItem = ri.next();
				todo.add(new GenericComparison(comparison.getRoot() + "[" + index + "]", leftItem, rightItem, this));
				index++;
			}
			if (li.hasNext() || ri.hasNext()) {
				throw new ComparisonException();
			}
			return todo;
		} else {
			todo.addAll(base.extend(comparison));
		}
		return todo;
	}

}
