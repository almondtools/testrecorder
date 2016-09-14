package net.amygdalum.testrecorder.deserializers.builder;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultSetupGenerator<SerializedNull> implements SetupGenerator<SerializedNull> {

	@Override
	public Class<SerializedNull> getAdaptedClass() {
		return SerializedNull.class;
	}

	@Override
	public Computation tryDeserialize(SerializedNull value, SetupGenerators generator) {
		return new Computation("null", value.getResultType());
	}

}
