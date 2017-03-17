package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WorkSet<T> implements Queue<T> {

    private Set<T> done;
    private Queue<T> elements;

    public WorkSet(Queue<T> base) {
        this.done = new LinkedHashSet<T>();
        this.elements = base;
    }
    
    public WorkSet() {
        this(new LinkedList<T>());
    }

    public Set<T> getDone() {
        return done;
    }

    @Override
    public int size() {
        return elements.size();
    }

    public boolean hasMoreElements() {
        return !elements.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return done.contains(o)
            || elements.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <S> S[] toArray(S[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o)
            | done.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!done.contains(element) && !elements.contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T element : c) {
            if (done.contains(element) || elements.contains(element)) {
                continue;
            }
            elements.add(element);
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c)
            | done.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c)
            | done.retainAll(c);
    }

    @Override
    public void clear() {
        elements.clear();
        done.clear();
    }

    @Override
    public boolean add(T e) {
        if (done.contains(e) || elements.contains(e)) {
            return false;
        }
        elements.add(e);
        return true;
    }

    @Override
    public boolean offer(T e) {
        if (done.contains(e) || elements.contains(e)) {
            return false;
        }
        elements.add(e);
        return true;
    }

    @Override
    public T remove() {
        T head = elements.remove();
        done.add(head);
        return head;
    }

    @Override
    public T poll() {
        T head = elements.poll();
        if (head == null) {
            return null;
        }
        done.add(head);
        return head;
    }

    @Override
    public T element() {
        return elements.element();
    }

    @Override
    public T peek() {
        return elements.peek();
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
