package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

/**
 * Serializing to SerializedEnum is restricted to enums. It is recommended not to use another enum implementation. 
 */
public class SerializedEnum extends AbstractSerializedReferenceType implements SerializedImmutableType {
	
	private String name;

	public SerializedEnum(Type type) {
		super(type);
	}

	public SerializedEnum withResult(Type resultType) {
		setResultType(resultType);
		return this;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
