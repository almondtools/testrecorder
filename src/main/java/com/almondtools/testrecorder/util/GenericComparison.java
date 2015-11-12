package com.almondtools.testrecorder.util;

import static com.almondtools.testrecorder.values.SerializedLiteral.isLiteral;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

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
		Queue<GenericComparison> todo = new LinkedList<>();
		todo.add(p);
		return equals(todo);
	}

	public static boolean equals(Queue<GenericComparison> todo) {
		while (!todo.isEmpty()) {
			GenericComparison current = todo.remove();
			if (!current.eval(todo)) {
				return false;
			}
		}
		return true;
	}

	public boolean eval(Queue<GenericComparison> todo) {
		if (left.getClass() != right.getClass()) {
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

	public static boolean equals(Object left, Field lfield, Object right, Field rfield, Queue<GenericComparison> todo) {
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
				todo.add(new GenericComparison(f1, f2));
				return true;
			}
		} catch (ReflectiveOperationException e) {
			return false;
		}
	}
	
	public static Object getValue(Field field, Object item) throws ReflectiveOperationException {
		boolean access = field.isAccessible();
		if (!access) {
			field.setAccessible(true);
		}
		try {
			return field.get(item);
		} finally {
			if (!access) {
				field.setAccessible(false);
			}
		}
	}
}