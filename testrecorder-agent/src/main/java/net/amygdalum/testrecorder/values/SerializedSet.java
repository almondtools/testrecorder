package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.typeArguments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Optionals;

/**
 * Serializing to SerializedSet is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.Set (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has an add method that is sequence invariant (deserializers potentially call the add method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedSet extends AbstractSerializedReferenceType implements SerializedReferenceType, Collection<SerializedValue> {

	private Type componentType;
	private Set<SerializedValue> set;

	public SerializedSet(Class<?> type) {
		super(type);
		this.componentType = Object.class;
		this.set = new LinkedHashSet<>();
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
		componentType = inferType(getComponentTypeCandidates(), set, Object.class);
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<SerializedValue> iterator() {
		return set.iterator();
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return set.toArray(a);
	}

	@Override
	public boolean add(SerializedValue element) {
		boolean added = set.add(element);
		if (!satisfiesType(componentType, element)) {
			componentType = inferType(getComponentTypeCandidates(), set, Object.class);
		}
		return added;
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends SerializedValue> c) {
		boolean added = set.addAll(c);
		if (!satisfiesType(componentType, c)) {
			componentType = inferType(getComponentTypeCandidates(), set, Object.class);
		}
		return added;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return set.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return set.removeAll(c);
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return new ArrayList<>(set);
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
