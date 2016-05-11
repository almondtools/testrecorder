package net.amygdalum.testrecorder.util;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import net.amygdalum.testrecorder.DefaultConfig;
import net.amygdalum.testrecorder.SnapshotConfig;
import net.amygdalum.testrecorder.SnapshotInstrumentor;

public class InstrumentedClassLoaderRunner extends BlockJUnit4ClassRunner {

	public static InstrumentedClassLoader loader;

	public InstrumentedClassLoaderRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected TestClass createTestClass(Class<?> testClass) {
		return super.createTestClass(instrumented(testClass));
	}

	private Class<?> instrumented(Class<?> klass) {
		try {
			Instrumented instrumented = klass.getAnnotation(Instrumented.class);
			loader = createLoader(klass, instrumented);
			return loader.loadClass(klass.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private InstrumentedClassLoader createLoader(Class<?> klass, Instrumented instrumented) {
		try {
			SnapshotConfig config = instrumented == null ? new DefaultConfig() : instrumented.config().newInstance();
			SnapshotInstrumentor instrumentor = new SnapshotInstrumentor(config);

			String[] classes = instrumented == null ? new String[0] : instrumented.classes();
			return new InstrumentedClassLoader(instrumentor, klass, classes);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
