package net.amygdalum.testrecorder.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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

	public static <T> BiOptional<T> ofOptionals(Optional<T> first, Optional<T> second) {
		boolean firstPresent = first.isPresent();
		boolean secondPresent = second.isPresent();
		if (firstPresent && secondPresent) {
			return BiOptional.of(first.get(), second.get());
		}
		if (secondPresent) {
			return second(second.get());
		}
		if (firstPresent) {
			return first(first.get());
		}
		return empty();
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

	public <S> BiOptional<S> bimap(Function<T, S> first, Function<T, S> second) {
		return ofNullable(first.apply(this.first), second.apply(this.second));
	}

	public <S> BiOptional<S> biflatmap(Function<T, Optional<S>> first, Function<T, Optional<S>> second) {
		return ofOptionals(first.apply(this.first), second.apply(this.second));
	}
	
	public BiOptional<T> bifilter(Predicate<T> first, Predicate<T> second) {
		boolean firstOk = first.test(this.first);
		boolean secondOk = second.test(this.second);
		if (firstOk && secondOk) {
			return this;
		}
		if (firstOk) {
			return ofNullable(this.first, null);
		}
		if (secondOk) {
			return ofNullable(null, this.second);
		}
		return empty();
	}

	public <R> Optional<R> map(BiFunction<T, T, R> mapping) {
		if (first == null || second == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(mapping.apply(first, second));
	}

	public <R> Optional<R> map(BiFunction<T, T, R> mapping, Function<T, R> singleOnly) {
		if (first == null && second == null) {
			return Optional.empty();
		}
		if (first == null) {
			return Optional.ofNullable(singleOnly.apply(second));
		}
		if (second == null) {
			return Optional.ofNullable(singleOnly.apply(first));
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
