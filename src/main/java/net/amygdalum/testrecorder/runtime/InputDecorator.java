package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.GenericObject.copyArrayValues;
import static net.amygdalum.testrecorder.runtime.GenericObject.copyField;
import static net.amygdalum.testrecorder.util.Types.allFields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class InputDecorator<T> {

	private T o;
	private Map<Method, List<InvocationData>> invocationData;

	public InputDecorator(T o) {
		this.o = Mockito.spy(o);
		this.invocationData = new HashMap<>();
	}

	public InputDecorator<T> provide(String method, Object result) {
		return provide(method, new Class[0], result);
	}

	public InputDecorator<T> provide(String method, Class<?>[] argTypes, Object result, Object... args) {
		Method resolvedMethod = resolveMethod(method, argTypes);
		InvocationData data = new InvocationData(result, args);
		invocationData.compute(resolvedMethod, (key, value) -> {
			if (value == null) {
				value = new ArrayList<>();
			}
			value.add(data);
			return value;
		});
		return this;
	}

	private Method resolveMethod(String method, Class<?>[] argTypes) {
		try {
			return o.getClass().getDeclaredMethod(method, argTypes);
		} catch (ReflectiveOperationException e) {
			throw new InputDecoratorException(e);
		}
	}

	public T setup() {
		for (Map.Entry<Method, List<InvocationData>> entry : invocationData.entrySet()) {
			Method method = entry.getKey();
			List<InvocationData> data = entry.getValue();
			Iterator<InvocationData> itr = data.iterator();
			if (!data.isEmpty()) {
				try {
					Object mock = Mockito.doAnswer(new Answer<Object>() {

						@Override
						public Object answer(InvocationOnMock invocation) throws Throwable {
							InvocationData next = itr.next();
							sync(next.args, invocation.getArguments());
							return next.result;
						}
					}).when(o);
					Object[] args = Arrays.stream(method.getParameterTypes())
						.map(type -> Mockito.any(type))
						.toArray(Object[]::new);
					method.invoke(mock, args);
				} catch (ReflectiveOperationException e) {
					throw new InputDecoratorException(e);
				}
			}
		}
		return o;
	}

	private void sync(Object[] fromArgs, Object[] toArgs) {
		for (int i = 0; i < toArgs.length; i++) {
			sync(fromArgs[i], toArgs[i]);
		}
	}

	private void sync(Object from, Object to) {
		if (from.getClass() != to.getClass()) {
			throw new AssertionError("expected argument type " + from.getClass().getName() + ", but found " + to.getClass().getName());
		}
		Class<?> current = from.getClass();
		if (current.isArray()) {
			copyArrayValues(from, to);
			return;
		}
		for (Field field : allFields(current)) {
			copyField(field, from, to);
		}
	}

	private static class InvocationData {
		public Object result;
		public Object[] args;

		public InvocationData(Object result, Object[] args) {
			this.result = result;
			this.args = args;
		}

	}

}
