package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;

import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
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
	public Computation tryDeserialize(SerializedImmutable<Class<?>> value, SetupGenerators generator) {
		TypeManager types = generator.getTypes();
		types.registerImport(Class.class);
		types.staticImport(Matchers.class, "equalTo");

		Class<?> clazz = value.getValue();

		return new Computation(types.getRawTypeName(clazz), value.getResultType());
	}

}
