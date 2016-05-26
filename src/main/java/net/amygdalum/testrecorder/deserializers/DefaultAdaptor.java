package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedValue;

public abstract class DefaultAdaptor<T extends SerializedValue,G> implements Adaptor<T,G> {

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Class<? extends Adaptor<T,G>> parent() {
		return null;
	}
	
	public boolean equalResultTypes(SerializedValue value) {
		return equalTypes(value.getType(), value.getResultType());
	}

	public boolean assignableResultTypes(SerializedValue value) {
		return assignableTypes(value.getResultType(), value.getType());
	}

}
