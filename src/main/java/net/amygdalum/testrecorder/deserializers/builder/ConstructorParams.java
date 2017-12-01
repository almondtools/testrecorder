package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConstructorParams {

	private Constructor<?> constructor;
	private List<ConstructorParam> params;

	public ConstructorParams(Constructor<?> constructor) {
		this.constructor = constructor;
		this.params = createInitialParams(constructor);
	}

	public boolean hasAmbiguitiesWith(Constructor<?> constructor) {
		if (constructor.equals(this.constructor)) {
			return false;
		}
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		if (parameterTypes.length != params.size()) {
			return false;
		}
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			ConstructorParam param = params.get(i);
			Object value = param.getValue();
			if (value != null && !parameterType.isInstance(value)) {
				return false; 
			}
		}
		return true;
	}

	private static List<ConstructorParam> createInitialParams(Constructor<?> constructor) {
		int parameterCount = constructor.getParameterCount();
		List<ConstructorParam> arrayList = new ArrayList<>(parameterCount);
		for (int i = 0; i < parameterCount; i++) {
			arrayList.add(new ConstructorParam(constructor, i));
		}
		return arrayList;
	}

	public void add(ConstructorParam param) {
		int index = param.getParamNumber();
		Type type = constructor.getGenericParameterTypes()[index];
		param.assertType(type);
		params.set(index, param);
	}

	public void insertTypeCasts() {
		for (ConstructorParam param : params) {
			param.insertTypeCasts();
		}
		
	}

	public Object apply() throws ReflectiveOperationException {
		return constructor.newInstance(getValues());
	}

	public Class<?> getType() {
		return constructor.getDeclaringClass();
	}

	public Object[] getValues() {
		return params.stream()
			.map(ConstructorParam::getValue)
			.toArray();
	}
	
	public List<ConstructorParam> getParams() {
		return params;
	}
	
	public int size() {
	    return params.size();
	}

}
