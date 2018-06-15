package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;

public class ASerializedValue extends AbstractSerializedValue {
	
	private int id;

	public ASerializedValue(Class<?> type) {
		this(type, 0);
	}

	public ASerializedValue(Class<?> type, int id) {
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