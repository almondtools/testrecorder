package net.amygdalum.testrecorder.util.testobjects;

import java.util.function.Predicate;

public class Inner {

	public static class Negate implements Predicate<Boolean> {

		@Override
		public boolean test(Boolean t) {
			return !t;
		}

	}

	public static Negate negate() {
		return new Negate();
	}

}