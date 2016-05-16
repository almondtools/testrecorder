package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.Construction;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedObject;

public class BeanObjectAdaptor implements Adaptor<SerializedObject, ObjectToSetupCode> {

	@Override
	public Class<? extends Adaptor<SerializedObject, ObjectToSetupCode>> parent() {
		return DefaultObjectAdaptor.class;
	}

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Computation tryDeserialize(SerializedObject value, ObjectToSetupCode generator) throws DeserializationException {
		TypeManager types = generator.getTypes();
		try {
			String name = generator.localVariable(value, value.getType());
			
			Computation best = new Construction(name, value).computeBest(types, generator);

			generator.finishVariable(value);
			
			return best;
		} catch (ReflectiveOperationException | RuntimeException e) {
			generator.resetVariable(value);
			throw new DeserializationException(value.toString());
		}
	}

}
