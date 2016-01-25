package com.almondtools.testrecorder.util;

public interface InputProvider {

	Object requestInput(Class<?> clazz, String method, Object... args);

}