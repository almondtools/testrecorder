package net.amygdalum.testrecorder.util.testobjects;

import java.util.function.Predicate;

public class Odd implements Predicate<Integer> {

	private boolean isOdd(int n) {
		if (n % 2 == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean test(Integer t) {
		return isOdd(t);
	}
	
	public static Odd odd() {
		return new Odd();
	}

}