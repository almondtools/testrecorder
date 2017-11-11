package net.amygdalum.testrecorder.runtime;

import java.util.Arrays;

import mockit.MockUp;
import net.amygdalum.testrecorder.util.Reflections;

public class FakeClass<T> extends MockUp<T> {

	public FakeClass() {
	}

	public void verify() {
		try {
			FakeCalls<?>[] fakeCalls = Arrays.stream(getClass().getDeclaredFields())
				.filter(field -> FakeCalls.class.isAssignableFrom(field.getType()))
				.map(field -> {
					try {
						return Reflections.getValue(field, this);
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				})
				.map(value -> (FakeCalls<?>) value)
				.toArray(FakeCalls[]::new);
			for (FakeCalls<?> fakeCallsItem : fakeCalls) {
				fakeCallsItem.verify();
			}
		} catch (RuntimeException e) {
			throw new AssertionError(e.getCause().getMessage(), e.getCause());
		}
	}
}
