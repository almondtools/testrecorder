package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class ExpectedOutput implements OutputListener {

	private Queue<OutputExpectation> expected;

	public ExpectedOutput() {
		this.expected = new LinkedList<>();
	}

	public ExpectedOutput expect(Class<?> clazz, String method, Matcher<?>... args) {
		expected.add(new OutputExpectation(clazz, method, args));
		return this;
	}

	@Override
	public void notifyOutput(Class<?> clazz, String method, Object... args) {
		OutputExpectation expectedOutput = expected.remove();
		expectedOutput.verify(clazz, method, args);
	}
	
	public void verify() {
		if (!expected.isEmpty()) {
			StringBuilder message = new StringBuilder("expected (but not found) output :\n");
			for (OutputExpectation outputExpectation : expected) {
				message.append(outputExpectation.expected()).append(",\n");
			}
			throw new AssertionError(message.toString());
		}
	}

	private static class OutputExpectation {

		private Class<?> clazz;
		private String method;
		private Matcher<?>[] args;

		public OutputExpectation(Class<?> clazz, String method, Matcher<?>[] args) {
			this.clazz = clazz;
			this.method = method;
			this.args = args;
		}

		public void verify(Class<?> clazz, String method, Object... args) {
			if (!this.clazz.equals(clazz)) {
				throw new AssertionError("expected output "+ expected() + ", but found " + found(clazz, method, args));
			}
			if (!this.method.equals(method)) {
				throw new AssertionError("expected output "+ expected() + ", but found " + found(clazz, method, args));
			}
			for (int i = 0; i < args.length; i++) {
				if (!this.args[i].matches(args[i])) {
					throw new AssertionError("expected output "+ expected() + ", but found " + found(clazz, method, args));
				}
			}
		}

		public String expected() {
			StringDescription description = new StringDescription();
			description.appendText(this.clazz.getSimpleName() + "." + this.method);
			description.appendList("(", ", ", ")", asList(args));
			return description.toString();
		}

		public String found(Class<?> clazz, String method, Object... args) {
			return clazz.getSimpleName() + "." + method + Stream.of(args)
				.map(arg -> arg.toString())
				.collect(joining(", ", "(", ")"));
		}

	}
}
