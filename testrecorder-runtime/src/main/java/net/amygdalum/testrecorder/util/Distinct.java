package net.amygdalum.testrecorder.util;

import java.util.function.Predicate;

public class Distinct implements Predicate<Object> {

	private static final int LEN = 113;

	private boolean containsNull = false;
	private Object[][] table;
	

	public Distinct() {
		table = new Object[LEN][];
	}

	public static Distinct distinct() {
		return new Distinct();
	}

	@Override
	public boolean test(Object o) {
		int slot = System.identityHashCode(o) % LEN;
		Object[] row = table[slot];
		if (o == null) {
			if (containsNull) {
				return false;
			}
			containsNull = true;
			return true;
		}
		if (row == null) {
			row = new Object[1];
			table[slot] = row;
			row[0] = o;
			return true;
		}
		int pos = 0;
		while (pos < row.length && row[pos] != null) {
			if (row[pos] == o)  {
				return false;
			}
			pos++;
		}
		if (pos >= row.length) {
			Object[] newRow = new Object[row.length * 2];
			System.arraycopy(row, 0, newRow, 0, row.length);
			row = newRow;
			table[slot] = row;
		}
		row[pos] = o;
		
		return true;
	}

}