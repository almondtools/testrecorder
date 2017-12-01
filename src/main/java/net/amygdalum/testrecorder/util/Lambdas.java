package net.amygdalum.testrecorder.util;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

public final class Lambdas {

	private static final Pattern LAMBDA_NAME_PATTERN = Pattern.compile("\\$\\$Lambda\\$\\d+/\\d+");

	private Lambdas() {
	}

	public static boolean isSerializableLambda(Type type) {
		Class<?> baseType = Types.baseType(type);
		if (LAMBDA_NAME_PATTERN.matcher(baseType.getName()).find()) {
			try {
				Method writeReplace = baseType.getDeclaredMethod("writeReplace");
				return writeReplace != null;
			} catch (NoSuchMethodException e) {
				return false;
			}

		}
		return false;
	}

	public static SerializedLambda serializeLambda(Object object) {
		try {
			Method writeReplace = object.getClass().getDeclaredMethod("writeReplace");
			return Reflections.accessing(writeReplace).call(m -> (SerializedLambda) m.invoke(object));
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}
}
