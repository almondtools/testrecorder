package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.asm.ByteCode.argumentTypesFrom;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;
import static net.amygdalum.testrecorder.util.Types.getDeclaredMethod;

import java.lang.reflect.Method;

import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.types.VirtualMethodSignature;

public class ContextSnapshotFactory {

	public static final ContextSnapshotFactory NULL = new ContextSnapshotFactory("null", null, null, null) {
		@Override
		public synchronized VirtualMethodSignature signature(ClassLoader loader) {
			return VirtualMethodSignature.NULL;
		}
	};

	private String key;

	private String className;
	private String methodName;
	private String methodDesc;

	public ContextSnapshotFactory(String key, String className, String methodName, String methodDesc) {
		this.key = key;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}

	public synchronized VirtualMethodSignature signature(ClassLoader loader) {
		try {
			Class<?> clazz = classFrom(className, loader);
			Method method = getDeclaredMethod(clazz, methodName, argumentTypesFrom(methodDesc, loader));
			return VirtualMethodSignature.fromDescriptor(clazz, method);
		} catch (RuntimeException | ReflectiveOperationException e) {
			throw new SerializationException(e);
		}
	}

	public ContextSnapshot createSnapshot(ClassLoader loader) {
		return new ContextSnapshot(System.currentTimeMillis(), key, signature(loader));
	}

}
