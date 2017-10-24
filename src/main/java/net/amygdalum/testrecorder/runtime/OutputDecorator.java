package net.amygdalum.testrecorder.runtime;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;

import net.amygdalum.testrecorder.util.Types;

public class OutputDecorator<T> {

	private static final Map<Integer, Map<Method, List<InvocationData>>> invocations = new LinkedHashMap<>();

	private T o;
	private Map<Method, List<InvocationData>> invocationData;

	public OutputDecorator(T o) {
		this.o = Mockito.spy(o);
		this.invocationData = new HashMap<>();
	}

	public OutputDecorator<T> expect(String method, String caller, Class<?>[] argTypes, Object result, Matcher<?>... args) {
		Method resolvedMethod = resolveMethod(method, argTypes);
		InvocationData data = new InvocationData(caller, result, args);
		invocationData.compute(resolvedMethod, (key, value) -> {
			if (value == null) {
				value = new ArrayList<>();
			}
			value.add(data);
			return value;
		});
		return this;
	}

	private Method resolveMethod(String method, Class<?>[] parameterTypes) {
		try {
			return Types.getDeclaredMethod(o.getClass(), method, parameterTypes);
		} catch (ReflectiveOperationException e) {
			throw new OutputDecoratorException(e);
		}
	}

	public T end() {
		for (Map.Entry<Method, List<InvocationData>> entry : invocationData.entrySet()) {
			Method method = entry.getKey();
			List<InvocationData> data = entry.getValue();
			Iterator<InvocationData> itr = data.iterator();
			if (!data.isEmpty()) {
				try {
					Object mock = Mockito.doAnswer(invocation -> {
						if (itr.hasNext()) {
							InvocationData next = itr.next();
							if (!callerMatches(invocation, next)) {
								throw new AssertionError("requested input from caller <" + caller(invocation) + "> was not recorded. Expected caller <" + next.caller + ">. Ensure that all call sites are recorded");
							}
							Object[] invocationArgs = invocation.getArguments();
							for (int i = 0; i < next.args.length; i++) {
								if (!next.args[i].matches(invocationArgs[i])) {
									String expected = Arrays.stream(next.args)
										.map(matcher -> StringDescription.toString(matcher))
										.collect(joining(", ", method.getName() + "(", ")"));
									String found = Arrays.stream(invocationArgs)
										.map(arg -> equalTo(arg))
										.map(matcher -> StringDescription.toString(matcher))
										.collect(joining(", ", method.getName() + "(", ")"));
									throw new AssertionError("expected output:\n" + expected + "\nbut found:\n" + found);
								}
							}
							next.called = true;
							return next.result;
						} else {
							Object[] invocationArgs = invocation.getArguments();
							String found = Arrays.stream(invocationArgs)
								.map(arg -> equalTo(arg))
								.map(matcher -> StringDescription.toString(matcher))
								.collect(joining(", ", method.getName() + "(", ")"));
							throw new AssertionError("unexpected output found:\n" + found + "\n\nIf the output was recorded ensure that all call sites were recorded");
						}
					}).when(o);
					Object[] args = Arrays.stream(method.getParameterTypes())
						.map(type -> Mockito.any(type))
						.toArray(Object[]::new);
					method.invoke(mock, args);
				} catch (ReflectiveOperationException e) {
					throw new OutputDecoratorException(e);
				}
			}
		}
		invocations.put(System.identityHashCode(o), invocationData);
		return o;

	}

	private String caller(InvocationOnMock invocation) {
		if (invocation instanceof Invocation) {
			return ((Invocation) invocation).getLocation().toString();
		}
		return "?";
	}

	private boolean callerMatches(InvocationOnMock invocation, InvocationData next) {
		if (invocation instanceof Invocation) {
			return ((Invocation) invocation).getLocation().toString().contains(next.caller);
		}
		return false;
	}

	public static Matcher<Object> verifies() {
		return new VerifyMatcher();
	}

	private static class InvocationData {
		public String caller;
		public Object result;
		public Matcher<?>[] args;
		public boolean called;

		public InvocationData(String caller, Object result, Matcher<?>[] args) {
			this.caller = caller;
			this.result = result;
			this.args = args;
			this.called = false;
		}

	}

	private static class VerifyMatcher extends TypeSafeDiagnosingMatcher<Object> {

		@Override
		protected boolean matchesSafely(Object item, Description mismatchDescription) {
			Map<Method, List<InvocationData>> invocationData = invocations.get(System.identityHashCode(item));
			for (Map.Entry<Method, List<InvocationData>> invocations : invocationData.entrySet()) {
				Method method = invocations.getKey();
				List<InvocationData> invocationsForMethod = invocations.getValue();
				for (InvocationData arguments : invocationsForMethod) {
					if (!arguments.called) {
						String found = Arrays.stream(arguments.args)
							.map(matcher -> StringDescription.toString(matcher))
							.collect(joining(", ", method.getName() + "(", ")"));
						mismatchDescription.appendText("did not receive: " + found);
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a sequence of outputs");
		}

	}

}