package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.mostSpecialOf;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.typeArguments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;

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

	public SerializedSet(Class<?> type) {
		super(type);
		this.set = new LinkedHashSet<>();
	}

	public SerializedSet with(Collection<SerializedValue> values) {
		set.addAll(values);
		return this;
	}

	public SerializedSet with(SerializedValue... values) {
		return with(asList(values));
	}

	public Type getComponentType() {
		Type[] candidates = Arrays.stream(getUsedTypes())
			.filter(type -> typeArguments(type).count() == 1)
			.map(type -> typeArgument(type, 0))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(this::satisfiesComponentType)
			.toArray(Type[]::new);
		return mostSpecialOf(candidates)
			.orElse(Object.class);
	}

	public boolean satisfiesComponentType(Type type) {
		Class<?> baseType = baseType(type);
		return set.stream()
			.filter(value -> value.getType() != null)
			.allMatch(value -> baseType.isAssignableFrom(value.getType()));
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
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
		return ValuePrinter.print(this);
	}
}
