package net.amygdalum.testrecorder.runtime;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.util.Types;

public class DefaultValue extends ValueFactory {

	public static final DefaultValue INSTANCE = new DefaultValue();

    public static Object of(Type type) {
    	Class<?> clazz = Types.baseType(type);
        return INSTANCE.newValue(clazz);
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
        } else {
            return null;
        }
	}

}