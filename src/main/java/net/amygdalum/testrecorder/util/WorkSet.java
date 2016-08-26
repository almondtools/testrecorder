package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class WorkSet<T> {

	private Set<T> done;
	private List<T> elements;

	public WorkSet() {
		this.done = new LinkedHashSet<T>();
		this.elements = new LinkedList<T>();
	}
	
	@SafeVarargs
	public WorkSet(T... element) {
		this.done = new LinkedHashSet<T>();
		this.elements = new LinkedList<T>(asList(element));
	}

	public WorkSet(Collection<T> initial) {
		this.done = new LinkedHashSet<T>();
		this.elements = new LinkedList<T>(initial);
	}

	public boolean contains(T element) {
		return done.contains(element)
			|| elements.contains(element);
	}

	public void enqueue(T element) {
		if (!done.contains(element) && !elements.contains(element)) {
			elements.add(element);
		}
	}

	public void enqueue(List<T> newelements) {
		for (T element : newelements) {
			enqueue(element);
		}
	}

	public boolean remove(T element) {
		return elements.remove(element);
	}

	public boolean hasMoreElements() {
		return elements.size() > 0;
	}

	public T dequeue() {
		if (elements.isEmpty()) {
			throw new NoSuchElementException();
		}
		T element = elements.remove(0);
		done.add(element);
		return element;
	}

	public List<T> dequeueAll() {
		List<T> result = new ArrayList<>();
		while (!elements.isEmpty()) {
			T element = elements.remove(0);
			done.add(element);
			result.add(element);
		}
		return result;
	}

	public List<T> getDone() {
		return new ArrayList<>(done);
	}
	
	@Override
	public String toString() {
		if (done.isEmpty()) {
			return elements.stream().map(Object::toString).collect(joining(", ", "{", "}"));
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append('{');
			buffer.append(elements.stream().map(Object::toString).collect(joining(", ")));
			buffer.append(" | ");
			buffer.append(done.stream().map(Object::toString).collect(joining(", ")));
			buffer.append('}');
			return buffer.toString();
		}
	}

}
