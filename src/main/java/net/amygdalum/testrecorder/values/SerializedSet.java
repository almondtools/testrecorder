package net.amygdalum.testrecorder.values;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedSet is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.Set (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has an add method that is sequence invariant (deserializers potentially call the add method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedSet implements SerializedReferenceType, Set<SerializedValue> {

	private Type type;
	private Class<?> valueType;
	private Set<SerializedValue> set;
	
	public SerializedSet(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
		this.set = new LinkedHashSet<>();
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
	
	public Type getComponentType() {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		} else {
			return Object.class;
		}
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
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

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}
}
