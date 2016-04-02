package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TypeSelector.innerClasses;
import static net.amygdalum.testrecorder.TypeSelector.startingWith;
import static net.amygdalum.testrecorder.deserializers.TypeManager.parameterized;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapAdaptor implements Adaptor<SerializedMap, ObjectToSetupCode> {

	private DefaultMapAdaptor adaptor;
	
	public CollectionsMapAdaptor() {
		this.adaptor = new DefaultMapAdaptor();
	}

	@Override
	public Class<? extends Adaptor<SerializedMap, ObjectToSetupCode>> parent() {
		return DefaultMapAdaptor.class;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return innerClasses(Collections.class)
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(element -> Map.class.isAssignableFrom(element))
			.anyMatch(element -> element.equals(clazz));
	}

	@Override
	public Computation tryDeserialize(SerializedMap value, ObjectToSetupCode generator) {
		SerializedMap baseValue = new SerializedMap(parameterized(LinkedHashMap.class, null, value.getMapKeyType(), value.getMapValueType()), LinkedHashMap.class);
		baseValue.putAll(value);
		return adaptor.tryDeserialize(baseValue, generator);
	}

}
