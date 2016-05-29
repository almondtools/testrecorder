package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.util.List;

public class FailedInstantiationException extends GenericObjectException {

	private Class<?> clazz;
	private List<String> tries;

	public FailedInstantiationException(Class<?> clazz, List<String> tries) {
		this.clazz = clazz;
		this.tries = tries;
	}

	public List<String> getTries() {
		return tries;
	}

	@Override
	public String getMessage() {
		return "failed to instantiate " + clazz.getName() + ", tried:\n" + tries.stream()
			.map(trie -> "\t- " + trie)
			.collect(joining("\n"));
	}
}
