package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.Types.isLiteral;

import java.lang.reflect.Array;

public class NonNullValue extends ValueFactory {

	public static final NonNullValue INSTANCE = new NonNullValue();

    public static Object of(Class<?> clazz) {
        return INSTANCE.newValue(clazz);
    }

    @Override
	public String getDescription(Class<?> clazz) {
	    if (clazz.isArray()) {
			return "new " + clazz.getComponentType().getSimpleName() + "[0]";
		} else if (clazz.isInterface()) {
			return "proxy " + clazz.getSimpleName() + "()";
		} else if (!isLiteral(clazz) && !clazz.isEnum()) {
			return "new " + clazz.getSimpleName() + "()";
		} else {
		    return super.getDescription(clazz);
		}
	}

	@Override
	public Object newValue(Class<?> clazz) {
        if (clazz == boolean.class) {
            return false;
        } else if (clazz == char.class) {
            return (char) 0;
        } else if (clazz == byte.class) {
            return (byte) 0;
        } else if (clazz == short.class) {
            return (short) 0;
        } else if (clazz == int.class) {
            return (int) 0;
        } else if (clazz == float.class) {
            return (float) 0;
        } else if (clazz == long.class) {
            return (long) 0;
        } else if (clazz == double.class) {
            return (double) 0;
        } else if (clazz == String.class) {
            return "";
        } else if (clazz.isArray()) {
            return Array.newInstance(clazz.getComponentType(), 0);
        } else if (clazz.isInterface()) {
            return GenericObject.newProxy(clazz);
        } else if (clazz.isEnum()) {
            return GenericObject.newEnum(clazz);
        } else {
            return GenericObject.newInstance(clazz);
        }
	}

}