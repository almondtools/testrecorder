package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.types.Computation.expression;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultSetupGenerator<SerializedNull> implements SetupGenerator<SerializedNull> {

	@Override
	public Class<SerializedNull> getAdaptedClass() {
		return SerializedNull.class;
	}

	@Override
	public Computation tryDeserialize(SerializedNull value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		TypeManager types = context.getTypes();
		
		return expression("null", types.mostSpecialOf(value.getUsedTypes()).orElse(null));
	}

}
