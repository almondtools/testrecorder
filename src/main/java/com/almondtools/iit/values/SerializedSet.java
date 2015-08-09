package com.almondtools.iit.values;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.SerializedValueVisitor;
import com.almondtools.iit.visitors.SerializedValuePrinter;

public class SerializedSet implements SerializedValue, Set<SerializedValue> {

	private Set<SerializedValue> set;
	
	public SerializedSet() {
		set = new LinkedHashSet<>();
	}
	
	@Override
	public Class<?> getType() {
		return Set.class;
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitSet(this);
	}

	public int size() {
		return set.size();
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public boolean contains(Object o) {
		return set.contains(o);
	}

	public Iterator<SerializedValue> iterator() {
		return set.iterator();
	}

	public Object[] toArray() {
		return set.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return set.toArray(a);
	}

	public boolean add(SerializedValue e) {
		return set.add(e);
	}

	public boolean remove(Object o) {
		return set.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	public boolean addAll(Collection<? extends SerializedValue> c) {
		return set.addAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return set.retainAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return set.removeAll(c);
	}

	public void clear() {
		set.clear();
	}

	public boolean equals(Object o) {
		return set.equals(o);
	}

	public int hashCode() {
		return set.hashCode();
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}
}
