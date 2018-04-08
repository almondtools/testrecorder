package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
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
	public Computation tryDeserialize(SerializedObject value, SetupGenerators generator, DeserializerContext context) throws DeserializationException {
		TypeManager types = context.getTypes();
		return context.forVariable(value, local -> {
			try {
				return new Construction(context, local, value).computeBest(types, generator);
			} catch (ReflectiveOperationException | RuntimeException e) {
				throw new DeserializationException("failed deserializing: " + value, e);
			}
		});
	}

}
