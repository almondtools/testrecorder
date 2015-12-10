package com.almondtools.testrecorder.util;

import java.lang.reflect.Field;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class ExpectedOutputRecorder extends BlockJUnit4ClassRunner {

	private ExpectedOutputRecorderClassLoader loader;
	private volatile ExpectedOutput expectedOutput;

	public ExpectedOutputRecorder(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		for (Field field : test.getClass().getDeclaredFields()) {
			if (field.getType() == ExpectedOutput.class) {
				field.setAccessible(true);
				field.set(test, expectedOutput);
			}
		}
		return test;
	}
	
	@Override
	protected TestClass createTestClass(Class<?> testClass) {
		return super.createTestClass(instrumented(testClass));
	}

	private Class<?> instrumented(Class<?> klass) {
		try {
			RecordOutput outputs = klass.getAnnotation(RecordOutput.class);
			loader = createLoader(klass, outputs);
			return loader.loadClass(klass.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private ExpectedOutputRecorderClassLoader createLoader(Class<?> klass, RecordOutput output) {
		String[] classes = output.value();
		ExpectedOutputRecorderClassLoader classLoader = new ExpectedOutputRecorderClassLoader(klass.getClassLoader(), klass.getName(), fetchExpectedOutput(), classes);
		return classLoader;
	}

	private synchronized ExpectedOutput fetchExpectedOutput() {
		if (expectedOutput == null) {
			expectedOutput = new ExpectedOutput();
		}
		return expectedOutput;
	}

}

