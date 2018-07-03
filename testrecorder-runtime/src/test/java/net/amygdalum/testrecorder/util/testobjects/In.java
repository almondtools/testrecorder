package net.amygdalum.testrecorder.util.testobjects;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

public class In implements Predicate<Integer> {

	private Set<Integer> integers;

	public In(int... integers) {
		this.integers = Arrays.stream(integers).mapToObj(Integer::valueOf).collect(toSet());
	}

	@Override
	public boolean test(Integer t) {
		return isIn(t);
	}
	
	private boolean isIn(Integer t) {
		for (Integer i : integers) {
			if (i.equals(t)) {
				return true;
			}
		}
		return false;
	}

	public static In in(int... integers) {
		return new In(integers);
	}

}