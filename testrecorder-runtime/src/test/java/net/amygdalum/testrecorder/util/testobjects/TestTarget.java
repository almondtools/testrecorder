package net.amygdalum.testrecorder.util.testobjects;

import java.util.function.Predicate;

public class TestTarget implements Predicate<Integer> {

	private boolean isPrime(final int n) {
		for (int i = 2; i * i <= n; i++) {
			if ((n % i) == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean test(Integer t) {
		return isPrime(t);
	}

}