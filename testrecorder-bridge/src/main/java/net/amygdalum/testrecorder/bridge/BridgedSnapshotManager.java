package net.amygdalum.testrecorder.bridge;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

/**
 * This class is attached to the bootstrap classpath by SnapshotManager, to enable jre classes to load fakes outside of jre.
 * 
 * Loading it with another class loader will break all features provided by this class, so never do it
 */
public class BridgedSnapshotManager {

	public static final ThreadLocal<MethodHandle> LOCK = new ThreadLocal<>();

	public static volatile Object MANAGER;

	public static MethodHandle setupVariables;
	public static MethodHandle expectVariables;
	public static MethodHandle expectVariablesVoid;
	public static MethodHandle throwVariables;

	public static MethodHandle inputVariables;
	public static MethodHandle inputArguments;
	public static MethodHandle inputResult;
	public static MethodHandle inputVoidResult;

	public static MethodHandle outputVariables;
	public static MethodHandle outputArguments;
	public static MethodHandle outputResult;
	public static MethodHandle outputVoidResult;

	public static void setupVariables(Class<?> clazz, Object self, String signature, Object... args) throws Throwable {
		MethodHandle handle = setupVariables;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, clazz, self, signature, args);
		} finally {
			LOCK.set(null);
		}
	}

	public static void expectVariables(Object self, String signature, Object result, Object... args) throws Throwable {
		MethodHandle handle = expectVariables;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, self, signature, result, args);
		} finally {
			LOCK.set(null);
		}

	}

	public static void expectVariables(Object self, String signature, Object... args) throws Throwable {
		MethodHandle handle = expectVariablesVoid;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, self, signature, args);
		} finally {
			LOCK.set(null);
		}
	}

	public static void throwVariables(Throwable throwable, Object self, String signature, Object... args) throws Throwable {
		MethodHandle handle = throwVariables;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, throwable, self, signature, args);
		} finally {
			LOCK.set(null);
		}

	}

	public static int inputVariables(Object object, String method, Type resultType, Type[] paramTypes) throws Throwable {
		MethodHandle handle = inputVariables;
		if (handle == null || LOCK.get() != null) {
			return 0;
		}
		try {
			LOCK.set(handle);
			Object result = handle.invoke(MANAGER, object, method, resultType, paramTypes);
			if (result instanceof Integer) {
				return ((Integer) result).intValue();
			} else {
				return 0;
			}
		} finally {
			LOCK.set(null);
		}
	}

	public static void inputArguments(int id, Object... args) throws Throwable {
		MethodHandle handle = inputArguments;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, id, args);
		} finally {
			LOCK.set(null);
		}
	}

	public static void inputResult(int id, Object result) throws Throwable {
		MethodHandle handle = inputResult;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, id, result);
		} finally {
			LOCK.set(null);
		}
	}

	public static void inputVoidResult(int id) throws Throwable {
		MethodHandle handle = inputVoidResult;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, id);
		} finally {
			LOCK.set(null);
		}
	}

	public static int outputVariables(Object object, String method, Type resultType, Type[] paramTypes) throws Throwable {
		MethodHandle handle = outputVariables;
		if (handle == null || LOCK.get() != null) {
			return 0;
		}
		try {
			LOCK.set(handle);
			Object result = handle.invoke(MANAGER, object, method, resultType, paramTypes);
			if (result instanceof Integer) {
				return ((Integer) result).intValue();
			} else {
				return 0;
			}
		} finally {
			LOCK.set(null);
		}
	}

	public static void outputArguments(int id, Object... args) throws Throwable {
		MethodHandle handle = outputArguments;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, id, args);
		} finally {
			LOCK.set(null);
		}
	}

	public static void outputResult(int id, Object result) throws Throwable {
		MethodHandle handle = outputResult;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, id, result);
		} finally {
			LOCK.set(null);
		}
	}

	public static void outputVoidResult(int id) throws Throwable {
		MethodHandle handle = outputVoidResult;
		if (handle == null || LOCK.get() != null) {
			return;
		}
		try {
			LOCK.set(handle);
			handle.invoke(MANAGER, id);
		} finally {
			LOCK.set(null);
		}
	}

}
