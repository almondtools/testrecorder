package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;

import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultClassAdaptor extends DefaultAdaptor<SerializedImmutable<Class<?>>, ObjectToSetupCode> implements Adaptor<SerializedImmutable<Class<?>>, ObjectToSetupCode> {

	@Override
	public boolean matches(Type type) {
		return equalTypes(type, Class.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<Class<?>> value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerImport(Class.class);
		types.staticImport(Matchers.class, "equalTo");

		Class<?> clazz = value.getValue();

		return new Computation(types.getRawTypeName(clazz), value.getResultType());
	}

}
