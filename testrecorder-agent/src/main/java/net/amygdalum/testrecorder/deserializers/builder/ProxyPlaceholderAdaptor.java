package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.runtime.PlaceHolderInvocationHandler;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedPlaceholder;

public class ProxyPlaceholderAdaptor extends DefaultSetupGenerator<SerializedPlaceholder> implements SetupGenerator<SerializedPlaceholder> {

	@Override
	public Class<SerializedPlaceholder> getAdaptedClass() {
		return SerializedPlaceholder.class;
	}

	@Override
	public boolean matches(Type type) {
		return InvocationHandler.class.isAssignableFrom(baseType(type));
	}

	@Override
	public Computation tryDeserialize(SerializedPlaceholder value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(PlaceHolderInvocationHandler.class);

		return Computation.expression(newObject(types.getConstructorTypeName(PlaceHolderInvocationHandler.class)), PlaceHolderInvocationHandler.class);
	}

}
