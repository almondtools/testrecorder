package net.amygdalum.testrecorder.visitors.builder;

import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.Construction;
import net.amygdalum.testrecorder.visitors.DeserializationException;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class BeanObjectAdaptor implements Adaptor<SerializedObject, ObjectToSetupCode> {

	@Override
	public Class<? extends Adaptor<SerializedObject, ObjectToSetupCode>> parent() {
		return DefaultObjectAdaptor.class;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return true;
	}

	@Override
	public Computation tryDeserialize(SerializedObject value, TypeManager types, ObjectToSetupCode generator) throws DeserializationException {
		try {
			String name = generator.localVariable(value, value.getValueType());
			return new Construction(name, value).computeBest(types, generator);
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new DeserializationException();
		}
	}

}
