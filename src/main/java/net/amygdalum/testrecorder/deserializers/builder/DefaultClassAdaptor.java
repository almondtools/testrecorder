package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultClassAdaptor extends DefaultSetupGenerator<SerializedImmutable<Class<?>>> implements Adaptor<SerializedImmutable<Class<?>>, SetupGenerators> {

	@SuppressWarnings("rawtypes")
	@Override
	public Class<SerializedImmutable> getAdaptedClass() {
		return SerializedImmutable.class;
	}

	@Override
	public boolean matches(Type type) {
		return equalTypes(type, Class.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<Class<?>> value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(Class.class);

		Class<?> clazz = value.getValue();

		return expression(types.getRawClass(clazz), types.mostSpecialOf(value.getUsedTypes()).orElse(Class.class));
	}

}
