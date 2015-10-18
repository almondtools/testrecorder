package com.almondtools.util.objects;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class UtilityClassMatcher extends TypeSafeMatcher<Class<?>> {

	public UtilityClassMatcher() {
	}

	public static UtilityClassMatcher isUtilityClass() {
		return new UtilityClassMatcher();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should be declared final\n");
		description.appendText("and have a private default constructor\n");
		description.appendText("and have only static methods\n");
	}

	@Override
	protected boolean matchesSafely(Class<?> item) {
		if (!isFinal(item.getModifiers())) {
			return false;
		}
		try {
			Constructor<?> constructor = item.getDeclaredConstructor();
			if (constructor.isAccessible()) {
				return false;
			}
			constructor.setAccessible(true);
			constructor.newInstance();
		} catch (InvocationTargetException e) {
			if (!(e.getCause() instanceof RuntimeException)) {
				return false;
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			return false;
		}
		for (Method method : item.getDeclaredMethods()) {
			if (!isStatic(method.getModifiers())) {
				return false;
			}
		}
		return true;
	}

}
