package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedImmutable is restricted to objects of a class that complies with following criteria:
 * - it is a class with immutable values (like BigInteger, BigDecimal ...)
 * - each custom serialized immutable class needs its own deserializer
 *    
 */
public class SerializedImmutable<V> extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private V value;

	public SerializedImmutable(Class<?> type) {
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
	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
