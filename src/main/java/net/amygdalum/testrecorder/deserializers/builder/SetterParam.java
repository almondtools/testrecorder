package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedField;

public class SetterParam {

	private Method method;
	private Type type;
	private SerializedField field;
	private Object value;

	public SetterParam(Method method, Type type, SerializedField field, Object value) {
		this.method = method;
		this.type = type;
		this.field = field;
		this.value = value;
	}
	
	public void apply(Object base) throws ReflectiveOperationException {
		method.invoke(base, value);
	}
	
	public String getName() {
		return method.getName();
	}
	
	public Type getType() {
		return type;
	}
	
	public SerializedField getField() {
		return field;
	}
	
	public Object getValue() {
        return value;
    }

	public SerializedValue computeSerializedValue() {
		return field.getValue();
	}

	@Override
	public String toString() {
		return method.toString() + "=" + field.getValue() + "=> " + field.getName();
	}
}
