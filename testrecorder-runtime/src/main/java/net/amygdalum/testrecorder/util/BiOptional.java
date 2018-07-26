package net.amygdalum.testrecorder.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BiOptional<T> {

	private static final BiOptional<?> EMPTY = new BiOptional<>();

	private T first;
	private T second;

	private BiOptional(T first, T second) {
		this.first = Objects.requireNonNull(first);
		this.second = Objects.requireNonNull(second);
	}

	private BiOptional(T first, boolean second) {
		this.first = Objects.requireNonNull(first);
	}

	private BiOptional(boolean first, T second) {
		this.second = Objects.requireNonNull(second);
	}

	private BiOptional() {
	}

	public static <T> BiOptional<T> ofNullable(T first, T second) {
		if (first == null && second == null) {
			return empty();
		}
		if (first == null) {
			return second(second);
		}
		if (second == null) {
			return first(first);
		}
		return BiOptional.of(first, second);
	}

	public static <T> BiOptional<T> of(T first, T second) {
		return new BiOptional<>(first, second);
	}

	public static <T> BiOptional<T> empty() {
		@SuppressWarnings("unchecked")
		BiOptional<T> t = (BiOptional<T>) EMPTY;
		return t;
	}

	public static <T> BiOptional<T> first(T first) {
		return new BiOptional<T>(first, false);
	}

	public static <T> BiOptional<T> second(T second) {
		return new BiOptional<T>(false, second);
	}

	public <R> Optional<R> map(BiFunction<T, T, R> mapping) {
		if (first == null || second == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(mapping.apply(first, second));
	}

	public <R> Optional<R> map(BiFunction<T, T, R> mapping, R onlyDefault) {
		if (first == null && second == null) {
			return Optional.empty();
		}
		if (first == null || second == null) {
			return Optional.of(onlyDefault);
		}
		return Optional.ofNullable(mapping.apply(first, second));
	}
	
	public <R> Optional<R> map(BiFunction<T, T, R> mapping, R firstOnlyDefault, R secondOnlyDefault) {
		if (first == null && second == null) {
			return Optional.empty();
		}
		if (first == null) {
			return Optional.of(secondOnlyDefault);
		}
		if (second == null) {
			return Optional.of(firstOnlyDefault);
		}
		return Optional.ofNullable(mapping.apply(first, second));
	}
	
	public <R> Optional<R> map(BiFunction<T, T, R> mapping, Function<T, R> firstOnly, Function<T, R> secondOnly) {
		if (first == null && second == null) {
			return Optional.empty();
		}
		if (first == null) {
			return Optional.ofNullable(secondOnly.apply(second));
		}
		if (second == null) {
			return Optional.ofNullable(firstOnly.apply(first));
		}
		return Optional.ofNullable(mapping.apply(first, second));
	}
	
}
