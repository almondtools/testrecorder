package net.amygdalum.testrecorder.deserializers.builder;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultAdaptor<SerializedNull, ObjectToSetupCode> implements Adaptor<SerializedNull, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedNull value, TypeManager types, ObjectToSetupCode generator) {
		return new Computation("null");
	}

}
