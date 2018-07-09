package net.amygdalum.testrecorder.util;

import java.util.function.Function;

public class Exceptional<T> {

	private Throwable throwable;
	private T value;

	public Exceptional(T value) {
		this.value = value;
	}

	public Exceptional(Throwable throwable) {
		this.throwable = throwable;
	}

	public static <T> Exceptional<T> throwing(Throwable throwable) {
		return new Exceptional<>(throwable);
	}

	public static <T> Exceptional<T> success(T value) {
		return new Exceptional<>(value);
	}

	public T andRecover(T defaultValue) {
		if (throwable == null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	public T andRecover(Function<Throwable, T> defaultValue) {
		if (throwable == null) {
			return value;
		} else {
			return defaultValue.apply(throwable);
		}
	}

	public T orFail() throws Throwable {
		if (throwable == null) {
			return value;
		}
		throw throwable;
	}

	public <E extends Exception> T orFail(Function<Throwable, E> exceptionMapper) throws E {
		if (throwable == null) {
			return value;
		}
		throw exceptionMapper.apply(throwable);
	}
}
