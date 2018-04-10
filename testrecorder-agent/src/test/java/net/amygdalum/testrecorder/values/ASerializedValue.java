package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.AbstractSerializedValue;

public class ASerializedValue extends AbstractSerializedValue {
	
	private int id;

	public ASerializedValue(Type type) {
		this(type, 0);
	}

	public ASerializedValue(Type type, int id) {
		super(type);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return null;
	}

}