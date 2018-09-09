package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedAggregateType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Types;

/**
 * Serializing to SerializedArray is restricted to arrays of any variant. It is recommended not to use another serialized array implementation. 
 */
public class SerializedArray extends AbstractSerializedReferenceType implements SerializedAggregateType {

	private Type componentType;
	private List<SerializedValue> array;

	public SerializedArray(Class<?> type) {
		super(type);
		this.componentType = type.getComponentType();
		this.array = new ArrayList<>();
	}
	
	@Override
	public List<SerializedValue> elements() {
		return array.stream()
			.distinct()
			.collect(toList());
	}

	public Type getComponentType() {
		return componentType;
	}

	public Class<?> getRawType() {
		return baseType(getComponentType());
	}

	public SerializedValue[] getArray() {
		return array.toArray(new SerializedValue[0]);
	}

	public List<SerializedValue> getArrayAsList() {
		return array;
	}

	private Stream<Type> getComponentTypeCandidates() {
		return Arrays.stream(getUsedTypes())
			.filter(Types::isArray)
			.map(Types::component);
	}

	@Override
	public void useAs(Type type) {
		super.useAs(type);
		if (componentType == null || !baseType(componentType).isPrimitive()) {
			componentType = inferType(getComponentTypeCandidates(), array, getType().getComponentType());
		}
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return elements();
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitReferenceType(this);
	}
	
	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitAggregateType(this);
	}

	public SerializedValue get(int index) {
		return array.get(index);
	}

	public int size() {
		return array.size();
	}

	public void add(SerializedValue value) {
		array.add(value);
		if (!satisfiesType(componentType, value)) {
			componentType = inferType(getComponentTypeCandidates(), array, getType().getComponentType());
		}
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
