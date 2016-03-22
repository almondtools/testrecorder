package net.amygdalum.testrecorder.visitors.builder;

import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultNullAdaptor extends DefaultAdaptor<SerializedNull, ObjectToSetupCode> implements Adaptor<SerializedNull, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedNull value, TypeManager types, ObjectToSetupCode generator) {
		return new Computation("null");
	}

}
