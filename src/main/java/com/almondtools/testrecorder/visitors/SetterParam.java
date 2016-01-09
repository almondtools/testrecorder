package com.almondtools.testrecorder.visitors;

import java.lang.reflect.Method;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.values.SerializedField;

public class SetterParam {

	private Method method;
	private SerializedField field;
	private Object value;

	public SetterParam(Method method, SerializedField field, Object value) {
		this.method = method;
		this.field = field;
		this.value = value;
	}
	
	public void apply(Object base) throws ReflectiveOperationException {
		method.invoke(base, value);
	}
	
	public String getName() {
		return method.getName();
	}
	
	public SerializedField getField() {
		return field;
	}

	public SerializedValue computeValue() {
		return field.getValue();
	}

	@Override
	public String toString() {
		return method.toString() + "=" + field.getValue() + "=> " + field.getName();
	}
}
