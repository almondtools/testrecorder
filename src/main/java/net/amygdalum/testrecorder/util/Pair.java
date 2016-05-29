package net.amygdalum.testrecorder.util;

import java.util.Objects;

public class Pair<T1, T2> {

	private T1 element1;
	private T2 element2;

	public Pair(T1 element1, T2 element2) {
		this.element1 = element1;
		this.element2 = element2;
	}

	public T1 getElement1() {
		return element1;
	}

	public T2 getElement2() {
		return element2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(element1, element2) + 17;
	}

	@SuppressWarnings("unchecked")
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
		Pair<T1, T2> that = (Pair<T1, T2>) obj;
		return Objects.equals(this.element1, that.element1)
			&& Objects.equals(this.element2, that.element2);
	}

}
