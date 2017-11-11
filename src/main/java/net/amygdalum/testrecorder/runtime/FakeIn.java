package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.GenericObject.copyArrayValues;
import static net.amygdalum.testrecorder.runtime.GenericObject.copyField;
import static net.amygdalum.testrecorder.util.Types.allFields;

import java.lang.reflect.Field;

public class FakeIn<T> extends FakeCalls<T>{

	public FakeIn(Object instance, String method, Class<?>... parameterTypes) {
		super(instance, method, parameterTypes);
	}
	public FakeIn(Class<?> clazz, String method, Class<?>... parameterTypes) {
		super(clazz, method, parameterTypes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T handleInvocationData(Object[] arguments, InvocationData next) {
			sync(next.args, arguments);
			return (T) next.result;
	}

	private void sync(Object[] fromArgs, Object[] toArgs) {
		for (int i = 0; i < toArgs.length; i++) {
			sync(fromArgs[i], toArgs[i]);
		}
	}

	private void sync(Object from, Object to) {
		Class<?> current = from.getClass();
		if (current.isArray()) {
			copyArrayValues(from, to);
			return;
		}
		for (Field field : allFields(current)) {
			copyField(field, from, to);
		}
	}

	
}
