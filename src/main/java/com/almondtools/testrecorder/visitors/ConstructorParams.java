package com.almondtools.testrecorder.visitors;

import static com.almondtools.testrecorder.util.GenericObject.getDefaultValue;
import static com.almondtools.testrecorder.values.SerializedLiteral.isLiteral;
import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
import static com.almondtools.testrecorder.values.SerializedNull.nullInstance;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.values.SerializedField;

public class ConstructorParams {

	private Constructor<?> constructor;
	private SerializedField[] fields;
	private Object[] values;

	public ConstructorParams(Constructor<?> constructor) {
		this.constructor = constructor;
		this.fields = new SerializedField[constructor.getParameterCount()];
		this.values = createDefaultArgumetns(constructor);
	}

	private static Object[] createDefaultArgumetns(Constructor<?> constructor) {
		Object[] objects = new Object[constructor.getParameterCount()];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = getDefaultValue(constructor.getParameterTypes()[i]);
		}
		return objects;
	}

	public void add(ConstructorParam param) {
		int index = param.getParamNumber();
		fields[index] = param.getField();
		values[index] = param.getValue();
	}
	
	public Object apply() throws ReflectiveOperationException {
		return constructor.newInstance(values);
	}

	public Class<?> getType() {
		return constructor.getDeclaringClass();
	}
	
	public SerializedField[] getFields() {
		return fields;
	}

	public List<SerializedValue> getValues() {
		List<SerializedValue> serializedValues = new ArrayList<>(fields.length);
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] == null) {
				Class<?> parameterType = constructor.getParameterTypes()[i];
				if (parameterType == String.class) {
					serializedValues.add(nullInstance(String.class));
				} else if (isLiteral(parameterType)) {
					serializedValues.add(literal(parameterType, values[i]));
				} else {
					serializedValues.add(nullInstance(parameterType));
				}
			} else {
				serializedValues.add(fields[i].getValue());
			}
		}
		return serializedValues;
	}
	
}
