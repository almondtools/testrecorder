package net.amygdalum.testrecorder.util;

import java.util.Optional;
import java.util.stream.Stream;

public final class Optionals {

	private Optionals() {
	}

	public static <T> Stream<T> stream(Optional<T> o) {
		return o.map(Stream::of).orElseGet(Stream::empty);
	}

}
