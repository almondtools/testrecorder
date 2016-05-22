package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.typeArgument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
public class SerializedSet extends AbstractSerializedReferenceType implements SerializedReferenceType, Set<SerializedValue> {

	private Set<SerializedValue> set;
	
	public SerializedSet(Type type) {
		super(type);
		this.set = new LinkedHashSet<>();
	}

	public SerializedSet withResult(Type resultType) {
		setResultType(resultType);
		return this;
	}
	
	public Type getComponentType() {
		return typeArgument(getType(), 0)
			.orElse(typeArgument(getResultType(), 0)
				.orElse(Object.class));
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
	public List<SerializedValue> referencedValues() {
		return new ArrayList<>(set);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}
}
