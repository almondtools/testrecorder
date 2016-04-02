package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.GenericTypeResolver.findAllTypes;
import static net.amygdalum.testrecorder.values.GenericTypeResolver.resolve;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedList is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.List (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has an add method that is sequence invariant (deserializers potentially call the add method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedList implements SerializedReferenceType, List<SerializedValue> {

	private Type type;
	private Class<?> valueType;
	private List<SerializedValue> list;

	public SerializedList(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
		this.list = new ArrayList<>();
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public Class<?> getValueType() {
		return valueType;
	}
	
	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public Type getComponentType() {
		Set<Type> allTypes = findAllTypes(type);
		return allTypes.stream()
			.filter(type -> type instanceof ParameterizedType)
			.map(type -> (ParameterizedType) type)
			.filter(type -> type.getRawType().equals(List.class))
			.map(type -> resolve(allTypes, type.getActualTypeArguments()[0]))
			.findFirst()
			.orElse(Object.class);
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
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

	public boolean add(SerializedValue e) {
		return list.add(e);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends SerializedValue> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends SerializedValue> c) {
		return list.addAll(index, c);
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
	public String toString() {
		return accept(new ValuePrinter());
	}

}
