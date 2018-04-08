package net.amygdalum.testrecorder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class PassiveDeque<T> implements Deque<T> {

	private T passive;

	public PassiveDeque(T passive) {
		this.passive = passive;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Object[] toArray() {
		return new Object[]{passive};
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <S> S[] toArray(S[] a) {
		Class<?> clazz = a == null ? passive.getClass() : a.getClass().getComponentType();
        S[] array = (S[]) Array.newInstance(clazz, 1);
		array[0] = (S) passive;
        return array;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public void addFirst(T e) {
	}

	@Override
	public void addLast(T e) {
	}

	@Override
	public boolean offerFirst(T e) {
		return false;
	}

	@Override
	public boolean offerLast(T e) {
		return false;
	}

	@Override
	public T removeFirst() {
		return passive;
	}

	@Override
	public T removeLast() {
		return passive;
	}

	@Override
	public T pollFirst() {
		return passive;
	}

	@Override
	public T pollLast() {
		return passive;
	}

	@Override
	public T getFirst() {
		return passive;
	}

	@Override
	public T getLast() {
		return passive;
	}

	@Override
	public T peekFirst() {
		return passive;
	}

	@Override
	public T peekLast() {
		return passive;
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return false;
	}

	@Override
	public boolean add(T e) {
		return false;
	}

	@Override
	public boolean offer(T e) {
		return false;
	}

	@Override
	public T remove() {
		return passive;
	}

	@Override
	public T poll() {
		return passive;
	}

	@Override
	public T element() {
		return passive;
	}

	@Override
	public T peek() {
		return passive;
	}

	@Override
	public void push(T e) {
	}

	@Override
	public T pop() {
		return passive;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList(passive).iterator();
	}

	@Override
	public Iterator<T> descendingIterator() {
		return Arrays.asList(passive).iterator();
	}

}
