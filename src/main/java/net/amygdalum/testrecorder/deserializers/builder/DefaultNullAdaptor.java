package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Computation.expression;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultSetupGenerator<SerializedNull> implements SetupGenerator<SerializedNull> {

	@Override
	public Class<SerializedNull> getAdaptedClass() {
		return SerializedNull.class;
	}

	@Override
	public Computation tryDeserialize(SerializedNull value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		
		return expression("null", types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class));
	}

}
