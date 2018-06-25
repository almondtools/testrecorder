package net.amygdalum.testrecorder.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class WorkQueue<E> implements Queue<E> {

	private int size = 0;
	private Node<E> first;
	private Node<E> last;

	public WorkQueue() {
	}

	public WorkQueue(Collection<? extends E> c) {
		addAll(c);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		for (Node<E> x = first; x != null; x = x.next) {
			if (x.item == o) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new QueueIterator();
	}

	@Override
	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (Node<E> x = first; x != null; x = x.next) {
			result[i++] = x.item;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		int i = 0;
		Object[] result = a;
		for (Node<E> x = first; x != null; x = x.next) {
			result[i++] = x.item;
		}
		return a;
	}

	@Override
	public boolean remove(Object o) {
		for (Node<E> x = first; x != null; x = x.next) {
			if (x.item == o) {
				unlink(x);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (!contains(e)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E> iterator = c.iterator();
		boolean added = iterator.hasNext();
		while (iterator.hasNext()) {
			E e = iterator.next();
			linkLast(e);
		}
		return added;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removed = false;
        Iterator<?> iterator = iterator();
        while (iterator.hasNext()) {
            Object e = iterator.next();
			if (c.contains(e)) {
                iterator.remove();
                removed = true;
            }
        }
        return removed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean removed = false;
        Iterator<?> iterator = iterator();
        while (iterator.hasNext()) {
            Object e = iterator.next();
			if (!c.contains(e)) {
                iterator.remove();
                removed = true;
            }
        }
        return removed;
	}

	@Override
	public void clear() {
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        size = 0;
        first = null;
        last = null;
	}

	@Override
	public boolean add(E e) {
        linkLast(e);
		return true;
	}

	@Override
	public boolean offer(E e) {
		return add(e);
	}

	@Override
	public E remove() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        return unlink(first);
	}

	@Override
	public E poll() {
        if (first == null) {
            return null;
        }
        return unlink(first);
	}

	@Override
	public E element() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        return first.item;
	}

	@Override
	public E peek() {
        if (first == null) {
            return null;
        }
        return first.item;
	}

    private void linkLast(E e) {
        Node<E> l = last;
        Node<E> newLast = new Node<>(l, e, null);
        last = newLast;
        if (l == null) {
            first = newLast;
        } else {
            l.next = newLast;
        }
        size++;
    }

    private E unlink(Node<E> x) {
		E element = x.item;
		Node<E> prev = x.prev;
		Node<E> next = x.next;

		if (prev == null) {
			first = next;
		} else {
			prev.next = next;
			x.prev = null;
		}

		if (next == null) {
			last = prev;
		} else {
			next.prev = prev;
			x.next = null;
		}

		x.item = null;
		size--;
		return element;
	}

	private class QueueIterator implements Iterator<E> {

		private Node<E> next;

		public QueueIterator() {
			this.next = first;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public E next() {
			if (next == null) {
				throw new NoSuchElementException();
			}
			E item = next.item;
			next = next.next;
			return item;
		}

		@Override
		public void remove() {
			if (next == null) {
				unlink(last);
			} else {
				unlink(next.prev);
			}
		}
	}

	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;

		Node(Node<E> prev, E element, Node<E> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

}
