package net.amygdalum.testrecorder.runtime;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import mockit.Invocation;
import net.amygdalum.testrecorder.util.Types;

public abstract class FakeCalls<T> {

	private Object instance;
	private Method method;
	private Queue<InvocationData> invocationData;

	public FakeCalls(Object instance, String method, Class<?>[] parameterTypes) {
		this.instance = instance;
		this.method = resolveMethod(instance.getClass(), method, parameterTypes);
		this.invocationData = new LinkedList<>();
	}

	public FakeCalls(Class<?> clazz, String method, Class<?>[] parameterTypes) {
		this.method = resolveMethod(clazz, method, parameterTypes);
		this.invocationData = new LinkedList<>();
	}
	
	public Method getMethod() {
		return method;
	}
	
	public FakeCalls<T> provide(String caller, Object result, Object... args) {
		InvocationData data = new InvocationData(caller, result, args);
		invocationData.add(data);
		return this;
	}

	private static Method resolveMethod(Class<?> clazz, String method, Class<?>[] parameterTypes) {
		try {
			return Types.getDeclaredMethod(clazz, method, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new FakeCallException(e);
		}
	}

	public T next(Invocation invocation, Object... varargs) {
		Object calledInstance = invocation.getInvokedInstance();
		Member member = invocation.getInvokedMember();
		Object[] arguments = invocation.getInvokedArguments();
		String caller = callerOf(member);
		if (instance != null && instance != calledInstance) {
			throw new AssertionError("requested input for " + instance + " from " + calledInstance);
		}
		if (!method.equals(member)) {
			throw new AssertionError("requested input for " + member + " from " + method);
		}
		Iterator<InvocationData> invocationDataIterator = invocationData.iterator();
		while (invocationDataIterator.hasNext()) {
			InvocationData next = invocationDataIterator.next();
			if (next.matchesCaller(caller)) {
				T result = handleInvocationData(arguments, next);
				invocationDataIterator.remove();
				return result;
			}
		}
		throw new AssertionError("missing input for:\n" + member + Arrays.toString(arguments) + " called from " + caller + "\n\nIf the input was recorded ensure that all call sites are recorded");
	}

	public abstract T handleInvocationData(Object[] arguments, InvocationData next);

	protected String signatureFor(Object[] args) {
		return Arrays.stream(args)
			.map(arg -> arg instanceof Matcher<?> ? (Matcher<?>) arg : equalTo(arg))
			.map(matcher -> StringDescription.toString(matcher))
			.collect(joining(", ", method.getName() + "(", ")"));
	}

	private String callerOf(Member member) {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement element = stackTrace[i];
			if (member.getDeclaringClass().getName().equals(element.getClassName())
				&& member.getName().equals(element.getMethodName()) && i + 1 < stackTrace.length) {
				StackTraceElement next = stackTrace[i+1];
				return next.getClassName() + "." + next.getMethodName();
			}
		}
		return "unknown";
	}

	public void verify() {
		if (!invocationData.isEmpty()) {
			StringBuilder msg = new StringBuilder("expected but not found:");
			for (InvocationData invocationDataItem : invocationData) {
				String expected = signatureFor(invocationDataItem.args);
				msg.append("\nexpected but not received call " + expected);
			}
			throw new AssertionError(msg);
		}
	}

	protected static class InvocationData {
		public String caller;
		public Object result;
		public Object[] args;

		public InvocationData(String caller, Object result, Object[] args) {
			this.caller = caller;
			this.result = result;
			this.args = args;
		}

		public boolean matchesCaller(String caller) {
			return this.caller.equals(caller);
		}

	}

}
