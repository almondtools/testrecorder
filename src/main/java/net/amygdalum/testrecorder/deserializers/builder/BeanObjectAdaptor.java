package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.Construction;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedObject;

public class BeanObjectAdaptor implements SetupGenerator<SerializedObject> {

	@Override
	public Class<SerializedObject> getAdaptedClass() {
		return SerializedObject.class;
	}

	@Override
	public Class<? extends SetupGenerator<SerializedObject>> parent() {
		return DefaultObjectAdaptor.class;
	}

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Computation tryDeserialize(SerializedObject value, SetupGenerators generator) throws DeserializationException {
		TypeManager types = generator.getTypes();
		return generator.forVariable(value, value.getType(), local -> {
			try {
				return new Construction(local, value).computeBest(types, generator);
			} catch (ReflectiveOperationException | RuntimeException e) {
				throw new DeserializationException(value.toString());
			}
		});
	}

}
