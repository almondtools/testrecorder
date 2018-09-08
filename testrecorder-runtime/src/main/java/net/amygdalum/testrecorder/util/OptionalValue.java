package net.amygdalum.testrecorder.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class OptionalValue<T> {

	private static final OptionalValue<?> EMPTY = new OptionalValue<>(true);

	private boolean empty;
	private T content;

	private OptionalValue(boolean empty) {
		this.content = null;
		this.empty = empty;
	}

	public OptionalValue(T content) {
		this.content = content;
	}

	public static <T> OptionalValue<T> of(Optional<T> value) {
		if (value == null) {
			return of((T) null);
		}
		return value.isPresent()
			? of(value.get())
			: empty();
	}

	public static <T> OptionalValue<T> of(T value) {
		return new OptionalValue<>(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> OptionalValue<T> empty() {
		return (OptionalValue<T>) EMPTY;
	}

	public OptionalValue<T> filter(Predicate<T> predicate) {
		if (empty || !predicate.test(content)) {
			return empty();
		}
		return this;
	}

	public <S> OptionalValue<S> map(Function<? super T, ? extends S> mapper) {
		if (empty) {
			return empty();
		}
		return of(mapper.apply(content));
	}

	public <S> OptionalValue<S> flatMap(Function<? super T, OptionalValue<S>> mapper) {
		if (empty) {
			return empty();
		}
		return mapper.apply(content);
	}

	public T orElse(T other) {
		return empty
			? other
			: content;
	}

	public T orElseGet(Supplier<? extends T> other) {
		return empty
			? other.get()
			: content;
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (empty) {
			throw exceptionSupplier.get();
		}
		return content;
	}

	public void ifPresent(Consumer<? super T> consumer) {
		if (!empty) {
			consumer.accept(content);
		}
	}

	public void ifPresentOrElse(Consumer<? super T> consumer, Runnable emptyAction) {
		if (empty) {
			emptyAction.run();
		} else {
			consumer.accept(content);
		}
	}

	public Stream<T> stream() {
		if (empty) {
			return Stream.empty();
		} else {
			return Stream.of(content);
		}
	}
}