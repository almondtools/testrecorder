package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.util.Reflections.getValue;
import static net.amygdalum.testrecorder.util.Types.isLiteral;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import net.amygdalum.testrecorder.util.WorkSet;

public class GenericComparison {
	private static final GenericComparison NULL = new GenericComparison("<error>", null, null, null, true);

	private String root;
	private Object left;
	private Object right;
	private Boolean mismatch;
	private ComparisonStrategy strategy;

	public GenericComparison(String root, Object left, Object right) {
		this(root, left, right, new DefaultComparisonStrategy(), null);
	}

	public GenericComparison(String root, Object left, Object right, ComparisonStrategy strategy) {
		this(root, left, right, strategy, null);
	}

	public GenericComparison(String root, Object left, Object right, ComparisonStrategy strategy, Boolean mismatch) {
		this.root = root;
		this.left = left;
		this.right = right;
		this.strategy = strategy;
		this.mismatch = mismatch;
	}

	public String getRoot() {
		return root;
	}

	public Object getLeft() {
		return left;
	}

	public Object getRight() {
		return right;
	}

	public void setMismatch(Boolean mismatch) {
		this.mismatch = mismatch;
	}

	public static boolean equals(String root, Object o1, Object o2) {
		return equals(new GenericComparison(root, o1, o2));
	}

	public static boolean equals(String root, Object o1, Object o2, ComparisonStrategy strategy) {
		return equals(new GenericComparison(root, o1, o2, strategy));
	}

	public static boolean equals(GenericComparison p) {
		WorkSet<GenericComparison> todo = new WorkSet<>();
		todo.add(p);
		return equals(todo);
	}

	public static boolean equals(WorkSet<GenericComparison> todo) {
		while (todo.hasMoreElements()) {
			GenericComparison current = todo.remove();
			if (!current.eval(todo)) {
				return false;
			}
		}
		return true;
	}

	public boolean eval(WorkSet<GenericComparison> todo) {
		if (left == right) {
			return true;
		} else if (left == null || right == null) {
			return false;
		} else if (left.getClass() != right.getClass()) {
			return false;
		}
		Class<?> clazz = left.getClass();
		if (isLiteral(clazz)) {
			return left.equals(right);
		}
		if (clazz.isArray()) {
			int length = Array.getLength(left);
			if (length != Array.getLength(right)) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				todo.add(newComparison(i));
			}
		} else {
			try {
				todo.addAll(strategy.extend(this));
			} catch (ComparisonException e) {
				if (e.failed()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean eval(GenericComparator comparator, WorkSet<GenericComparison> todo) {
		if (left == right) {
			return true;
		}
		GenericComparatorResult compare = comparator.compare(this, todo);
		if (compare.isApplying()) {
			return compare.getResult();
		}
		if (left == null || right == null) {
			return false;
		}
		Class<?> clazz = left.getClass();
		if (isLiteral(clazz)) {
			return left.equals(right);
		}
		if (clazz.isArray()) {
			int length = Array.getLength(left);
			if (length != Array.getLength(right)) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				todo.add(newComparison(i));
			}
		} else {
			try {
				todo.addAll(strategy.extend(this));
			} catch (ComparisonException e) {
				if (e.failed()) {
					return false;
				}
			}
		}
		return true;
	}

	public static GenericComparison from(String root, String field, Object left, Object right) {
		try {
			Object f1 = getValue(field, left);
			Object f2 = getValue(field, right);
			String newRoot = root == null ? field : root + '.' + field;
			return new GenericComparison(newRoot, f1, f2);
		} catch (ReflectiveOperationException e) {
			return GenericComparison.NULL;
		}
	}

	public GenericComparison newComparison(String field) {
		try {
			Object f1 = getValue(field, left);
			Object f2 = getValue(field, right);
			String newRoot = root == null ? field : root + '.' + field;
			return new GenericComparison(newRoot, f1, f2, strategy);
		} catch (ReflectiveOperationException e) {
			return GenericComparison.NULL;
		}
	}

	public static GenericComparison from(String root, Field lfield, Object left, Field rfield, Object right) {
		try {
			Object f1 = getValue(lfield, left);
			Object f2 = getValue(rfield, right);
			String newRoot = root == null ? lfield.getName() : root + '.' + lfield.getName();
			return new GenericComparison(newRoot, f1, f2);
		} catch (ReflectiveOperationException e) {
			return GenericComparison.NULL;
		}
	}

	public GenericComparison newComparison(Field lfield, Field rfield) {
		try {
			Object f1 = getValue(lfield, left);
			Object f2 = getValue(rfield, right);
			String newRoot = root == null ? lfield.getName() : root + '.' + lfield.getName();
			return new GenericComparison(newRoot, f1, f2, strategy);
		} catch (ReflectiveOperationException e) {
			return GenericComparison.NULL;
		}
	}

	public static GenericComparison from(String root, int index, Object leftArray, Object rightArray) {
		try {
			String suffix = "[" + index + "]";
			String newRoot = root == null ? suffix : root + suffix;
			return new GenericComparison(newRoot, Array.get(leftArray, index), Array.get(rightArray, index));
		} catch (ArrayIndexOutOfBoundsException e) {
			return GenericComparison.NULL;
		}
	}

	public GenericComparison newComparison(int index) {
		try {
			String suffix = "[" + index + "]";
			String newRoot = root == null ? suffix : root + suffix;
			return new GenericComparison(newRoot, Array.get(left, index), Array.get(right, index), strategy);
		} catch (ArrayIndexOutOfBoundsException e) {
			return GenericComparison.NULL;
		}
	}

	public static void compare(WorkSet<GenericComparison> remainder, GenericComparator comparator) {
		while (remainder.hasMoreElements()) {
			GenericComparison current = remainder.remove();
			if (!current.eval(comparator, remainder)) {
				current.setMismatch(true);
			}
		}
	}

	public boolean isMismatch() {
		return mismatch == null ? false : mismatch;
	}

	@Override
	public int hashCode() {
		return 17 + System.identityHashCode(left) * 13 + System.identityHashCode(right) * 7;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenericComparison that = (GenericComparison) obj;
		return this.right == that.right
			&& this.left == that.left;
	}

	@Override
	public String toString() {
		return root + ":" + System.identityHashCode(left) + "/" + System.identityHashCode(right) + "=" + isMismatch();
	}

}