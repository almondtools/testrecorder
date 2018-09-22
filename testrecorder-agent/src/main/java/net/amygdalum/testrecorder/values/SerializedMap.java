package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.typeArguments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedAggregateType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Optionals;

/**
 * Serializing to SerializedMap is restricted to objects of a class that complies with following criteria:
 * - is a sub class of java.util.Map (deserializers can depend on the java.util.Set interface)
 * - has an empty public default constructor (deserializers potentially call the standard constructor)
 * - has a put method that is sequence invariant (deserializers potentially call the put method)
 * 
 * Serializing objects not complying to this criteria is possible, just make sure that their exists a custom deserializer for these objects  
 */
public class SerializedMap extends AbstractSerializedReferenceType implements SerializedAggregateType, Map<SerializedValue, SerializedValue> {

	private Type keyType;
	private Type valueType;
	private Map<SerializedValue, SerializedValue> map;

	public SerializedMap(Class<?> type) {
		super(type);
		this.keyType = Object.class;
		this.valueType = Object.class;
		this.map = new LinkedHashMap<>();
	}

	@Override
	public List<SerializedValue> elements() {
		return map.entrySet().stream()
			.flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
			.distinct()
			.collect(toList());
	}

	public Type getMapKeyType() {
		return keyType;
	}

	public Type getMapValueType() {
		return valueType;
	}

	private Stream<Type> getKeyTypeCandidates() {
		return Arrays.stream(getUsedTypes())
			.filter(type -> typeArguments(type).count() == 2)
			.flatMap(type -> Optionals.stream(typeArgument(type, 0)));
	}

	private Stream<Type> getValueTypeCandidates() {
		return Arrays.stream(getUsedTypes())
			.filter(type -> typeArguments(type).count() == 2)
			.flatMap(type -> Optionals.stream(typeArgument(type, 1)));
	}

	@Override
	public void useAs(Type type) {
		super.useAs(type);
		keyType = inferType(getKeyTypeCandidates(), map.keySet(), Object.class);
		valueType = inferType(getValueTypeCandidates(), map.values(), Object.class);
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitReferenceType(this);
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitAggregateType(this);
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public SerializedValue get(Object key) {
		return map.get(key);
	}

	public SerializedValue put(SerializedValue key, SerializedValue value) {
		SerializedValue replaced = map.put(key, value);
		if (!satisfiesType(keyType, key)) {
			keyType = inferType(getKeyTypeCandidates(), map.keySet(), Object.class);
		}
		if (!satisfiesType(valueType, value)) {
			valueType = inferType(getValueTypeCandidates(), map.values(), Object.class);
		}
		return replaced;
	}

	public SerializedValue remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map<? extends SerializedValue, ? extends SerializedValue> m) {
		map.putAll(m);
		if (!satisfiesType(keyType, m.keySet())) {
			keyType = inferType(getKeyTypeCandidates(), map.keySet(), Object.class);
		}
		if (!satisfiesType(valueType, m.values())) {
			valueType = inferType(getValueTypeCandidates(), map.values(), Object.class);
		}
	}

	public void clear() {
		map.clear();
	}

	public Set<SerializedValue> keySet() {
		return map.keySet();
	}

	public Collection<SerializedValue> values() {
		return map.values();
	}

	public Set<Map.Entry<SerializedValue, SerializedValue>> entrySet() {
		return map.entrySet();
	}

	@Override
	public List<SerializedValue> referencedValues() {
		List<SerializedValue> referenced = new ArrayList<>(map.keySet());
		referenced.addAll(map.values());
		return referenced;
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
