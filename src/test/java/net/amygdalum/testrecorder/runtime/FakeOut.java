package net.amygdalum.testrecorder.runtime;

import java.util.Arrays;

import org.hamcrest.Matcher;

public class FakeOut<T> extends FakeCalls<T> {

	public FakeOut(Object instance, String method, Class<?>... parameterTypes) {
		super(instance, method, parameterTypes);
	}

	public FakeOut(Class<?> clazz, String method, Class<?>... parameterTypes) {
		super(clazz, method, parameterTypes);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T handleInvocationData(Object[] arguments, InvocationData next) {
		Object[] args = next.args;
		if (!verify(args, arguments)) {
			String expected = signatureFor(args);
			String found = signatureFor(arguments);
			throw new AssertionError("expected output:\n" + expected + "\nbut found:\n" + found);
		}
		return (T) next.result;
	}

	private boolean verify(Object[] fromArgs, Object[] toArgs) {
		for (int i = 0; i < fromArgs.length; i++) {
			Object from = fromArgs[i];
			Object to = toArgs[i];
			if (from instanceof Matcher<?>) {
				if (!((Matcher<?>) from).matches(to)) {
					return false;
				}
			} else if (from == null) {
				if (to != null) {
					return false;
				}
			} else if (from != null) {
				if (from instanceof boolean[]) {
					if (!(to instanceof boolean[] && Arrays.equals((boolean[]) from, (boolean[]) to))) {
						return false;
					}
				} else if (from instanceof byte[]) {
					if (!(to instanceof byte[] && Arrays.equals((byte[]) from, (byte[]) to))) {
						return false;
					}
				} else if (from instanceof short[]) {
					if (!(to instanceof short[] && Arrays.equals((short[]) from, (short[]) to))) {
						return false;
					}
				} else if (from instanceof int[]) {
					if (!(to instanceof int[] && Arrays.equals((int[]) from, (int[]) to))) {
						return false;
					}
				} else if (from instanceof long[]) {
					if (!(to instanceof long[] && Arrays.equals((long[]) from, (long[]) to))) {
						return false;
					}
				} else if (from instanceof float[]) {
					if (!(to instanceof float[] && Arrays.equals((float[]) from, (float[]) to))) {
						return false;
					}
				} else if (from instanceof double[]) {
					if (!(to instanceof double[] && Arrays.equals((double[]) from, (double[]) to))) {
						return false;
					}
				} else if (from instanceof char[]) {
					if (!(to instanceof char[] && Arrays.equals((char[]) from, (char[]) to))) {
						return false;
					}
				} else if (from instanceof Object[]) {
					if (!(to instanceof Object[] && Arrays.equals((Object[]) from, (Object[]) to))) {
						return false;
					}
				} else if (!from.equals(to)) {
					return false;
				}
			}
		}
		return true;
	}

}
