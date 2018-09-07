package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ObjectBuilderAdaptor implements SetupGenerator<SerializedObject> {

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

		Type type = types.isHidden(value.getType())
			? types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class)
			: value.getType();

		return context.forVariable(value, type, local -> {
			try {
				return new BuilderConstruction(context, local, value).build(types, generator);
			} catch (ReflectiveOperationException | RuntimeException e) {
				throw new DeserializationException("failed deserializing with builder conventions: " + value, e);
			}
		});
	}

}
