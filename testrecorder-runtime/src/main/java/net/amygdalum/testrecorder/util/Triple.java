package net.amygdalum.testrecorder.util;

import java.util.Objects;

public class Triple<T1, T2,T3> {

	private T1 element1;
	private T2 element2;
    private T3 element3;

	public Triple(T1 element1, T2 element2, T3 element3) {
		this.element1 = element1;
		this.element2 = element2;
        this.element3 = element3;
	}

    @SuppressWarnings("unchecked")
    public static <T1,T2,T3> Triple<T1, T2, T3>[] zip(T1[] e1, T2[] e2, T3[] e3) {
        if (e1.length != e2.length || e2.length != e3.length) {
            throw new IllegalArgumentException();
        }
        Triple<T1, T2, T3>[] triples = new Triple[e1.length];
        for (int i = 0; i < triples.length; i++) {
            triples[i] = new Triple<>(e1[i], e2[i], e3[i]);
        }
        return triples;
    }

	public T1 getElement1() {
		return element1;
	}

	public T2 getElement2() {
		return element2;
	}
	
	public T3 getElement3() {
        return element3;
    }

	@Override
	public int hashCode() {
		return Objects.hash(element1, element2, element3) + 17;
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
		Triple<T1, T2,T3> that = (Triple<T1, T2, T3>) obj;
		return Objects.equals(this.element1, that.element1)
			&& Objects.equals(this.element2, that.element2)
			&& Objects.equals(this.element3, that.element3);
	}

}
