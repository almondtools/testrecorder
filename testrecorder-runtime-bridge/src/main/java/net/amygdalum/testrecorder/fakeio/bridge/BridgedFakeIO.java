package net.amygdalum.testrecorder.fakeio.bridge;

import java.lang.invoke.MethodHandle;

/**
 * This class is attached to the bootstrap classpath by FakeIO, to enable jre classes to load fakes outside of jre.
 * 
 * Loading it with another class loader will break all features provided by this class, so never do it
 */
public class BridgedFakeIO {

	public static final ThreadLocal<MethodHandle> LOCK = new ThreadLocal<>();

	public static MethodHandle callFake;
	public static Object NO_RESULT;

	public static Object callFake(String name, Object instance, String methodName, String methodDesc, Object... varargs) throws Throwable {
		if (callFake == null || LOCK.get() != null) {
			return NO_RESULT;
		}
		try {
			LOCK.set(callFake);
			return callFake.invoke(name, instance, methodName, methodDesc, varargs);
		} finally {
			LOCK.set(null);
		}
	}

}
