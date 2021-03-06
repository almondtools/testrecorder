package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.util.Types.isLiteral;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class NonDefaultValue extends ValueFactory {

    public static final NonDefaultValue INSTANCE = new NonDefaultValue();

	private static final Object PLACEHOLDER = new Object();
	
	private Map<Class<?>, Object> cache;
	
	public NonDefaultValue() {
		cache = new HashMap<>();
	}

    public static Object of(Class<?> clazz) {
        return INSTANCE.newValue(clazz);
    }

    @Override
    public String getDescription(Class<?> clazz) {
        if (clazz.isArray()) {
            return "new " + clazz.getComponentType().getSimpleName() + "[1]";
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
            return true;
        } else if (clazz == char.class) {
            return (char) 1;
        } else if (clazz == byte.class) {
            return (byte) 1;
        } else if (clazz == short.class) {
            return (short) 1;
        } else if (clazz == int.class) {
            return (int) 1;
        } else if (clazz == float.class) {
            return (float) 1;
        } else if (clazz == long.class) {
            return (long) 1;
        } else if (clazz == double.class) {
            return (double) 1;
        } else if (clazz == String.class) {
            return clazz.getSimpleName();
        } else {
        	Object object = cache.get(clazz);
        	if (object == PLACEHOLDER) {
        		throw new IllegalArgumentException("cancelling because of potentially recursive object graph");
        	} 
        	if (object == null) {
        		cache.put(clazz, PLACEHOLDER);
        		object = newReferenceValue(clazz);
                cache.put(clazz, object);
        	} 
        	return object;
        }
        	
    }

	private Object newReferenceValue(Class<?> clazz) {
        if (clazz.isArray()) {
            return Array.newInstance(clazz.getComponentType(), 1);
        } else if (clazz.isInterface()) {
            return GenericObject.newProxy(clazz);
        } else if (clazz.isEnum()) {
            return GenericObject.newEnum(clazz);
        } else {
            return GenericObject.newInstance(clazz);
        }
	}

}