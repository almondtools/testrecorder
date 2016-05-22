package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedArray is restricted to arrays of any variant. It is recommended not to use another serialized array implementation. 
 */
public class SerializedArray extends AbstractSerializedReferenceType implements SerializedReferenceType {

	private List<SerializedValue> array;

	public SerializedArray(Type type) {
		super(type);
		this.array = new ArrayList<>();
	}

	public SerializedArray with(SerializedValue... values) {
		array.addAll(asList(values));
		return this;
	}

	public Type getComponentType() {
		return component(getType());
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
	
	@Override
	public List<SerializedValue> referencedValues() {
		return new ArrayList<>(array);
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
	}
	
	public void add(SerializedValue value) {
		array.add(value);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}
	
}
