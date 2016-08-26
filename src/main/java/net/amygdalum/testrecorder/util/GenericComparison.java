package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.values.SerializedLiteral.isLiteral;

import java.lang.reflect.Field;

public class GenericComparison {
	private Object left;
	private Object right;

	public GenericComparison(Object left, Object right) {
		this.left = left;
		this.right = right;
	}

	public Object getLeft() {
		return left;
	}

	public Object getRight() {
		return right;
	}

	public static boolean equals(Object o1, Object o2) {
		return equals(new GenericComparison(o1, o2));
	}

	public static boolean equals(GenericComparison p) {
		WorkSet<GenericComparison> todo = new WorkSet<>();
		todo.enqueue(p);
		return equals(todo);
	}

	public static boolean equals(WorkSet<GenericComparison> todo) {
		while (todo.hasMoreElements()) {
			GenericComparison current = todo.dequeue();
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
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isSynthetic()) {
					continue;
				}
				if (!equals(left, field, right, field, todo)) {
					return false;
				}
			}
			clazz = clazz.getSuperclass();
		}
		return true;
	}

	public static boolean equals(Object left, Field lfield, Object right, Field rfield, WorkSet<GenericComparison> todo) {
		try {
			Object f1 = getValue(lfield, left);
			Object f2 = getValue(rfield, right);
			if (f1 == f2) {
				return true;
			} else if (f1 == null) {
				return false;
			} else if (f2 == null) {
				return false;
			} else if (f1.equals(f2)) {
				return true;
			} else {
				todo.enqueue(new GenericComparison(f1, f2));
				return true;
			}
		} catch (ReflectiveOperationException e) {
			return false;
		}
	}

	public static Object getValue(Field field, Object item) throws ReflectiveOperationException {
		return accessing(field).call(() -> field.get(item));
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

}