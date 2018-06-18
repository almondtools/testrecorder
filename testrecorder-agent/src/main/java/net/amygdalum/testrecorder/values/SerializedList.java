package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.typeArguments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Optionals;

/**
 * Serializing to SerializedList is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.List (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has an add method that is sequence invariant (deserializers potentially call the add method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedList extends AbstractSerializedReferenceType implements SerializedReferenceType, List<SerializedValue> {

	private Type componentType;
	private List<SerializedValue> list;

	public SerializedList(Class<?> type) {
		super(type);
		this.componentType = Object.class;
		this.list = new ArrayList<>();
	}

	public Type getComponentType() {
		return componentType;
	}

	private Stream<Type> getComponentTypeCandidates() {
		return Arrays.stream(getUsedTypes())
			.filter(type -> typeArguments(type).count() == 1)
			.flatMap(type -> Optionals.stream(typeArgument(type, 0)));
	}

	@Override
	public void useAs(Type type) {
		super.useAs(type);
		componentType = inferType(getComponentTypeCandidates(), list, Object.class);
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public Iterator<SerializedValue> iterator() {
		return list.iterator();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public boolean add(SerializedValue element) {
		boolean added = list.add(element);
		if (!satisfiesType(componentType, element)) {
			componentType = inferType(getComponentTypeCandidates(), list, Object.class);
		}
		return added;
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends SerializedValue> c) {
		boolean added = list.addAll(c);
		if (!satisfiesType(componentType, c)) {
			componentType = inferType(getComponentTypeCandidates(), list, Object.class);
		}
		return added;
	}

	public boolean addAll(int index, Collection<? extends SerializedValue> c) {
		boolean added = list.addAll(index, c);
		if (!satisfiesType(componentType, c)) {
			componentType = inferType(getComponentTypeCandidates(), list, Object.class);
		}
		return added;
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public void clear() {
		list.clear();
	}

	public SerializedValue get(int index) {
		return list.get(index);
	}

	public SerializedValue set(int index, SerializedValue element) {
		return list.set(index, element);
	}

	public void add(int index, SerializedValue element) {
		list.add(index, element);
		if (!satisfiesType(componentType, element)) {
			componentType = inferType(getComponentTypeCandidates(), list, Object.class);
		}
	}

	public SerializedValue remove(int index) {
		return list.remove(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<SerializedValue> listIterator() {
		return list.listIterator();
	}

	public ListIterator<SerializedValue> listIterator(int index) {
		return list.listIterator(index);
	}

	public List<SerializedValue> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return new ArrayList<>(list);
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
