package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ConstructorParams {

	private Constructor<?> constructor;
	private List<ConstructorParam> params;

	public ConstructorParams(Constructor<?> constructor) {
		this.constructor = constructor;
		this.params = createInitialParams(constructor);
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
		Class<?> type = constructor.getParameterTypes()[index];
		param.assertType(type);
		params.set(index, param);
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

}
