package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedImmutable is restricted to objects of a class that complies with following criteria:
 * - it is a class with immutable values (like BigInteger, BigDecimal ...)
 * - each custom serialized immutable class needs its own deserializer
 *    
 */
public class SerializedImmutable<V> extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private V value;

	public SerializedImmutable(Type type) {
		super(type);
	}

	public SerializedImmutable<V> withValue(V value) {
		this.value = value;
		return this;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
