package net.amygdalum.testrecorder.util;

import java.lang.instrument.UnmodifiableClassException;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import mockit.internal.startup.InstrumentationHolder;
import net.amygdalum.testrecorder.AllLambdasSerializableTransformer;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.SnapshotInstrumentor;
import net.amygdalum.testrecorder.TestRecorderAgentConfig;

public class InstrumentedClassLoaderRunner extends BlockJUnit4ClassRunner {

	public static InstrumentedClassLoader loader;

	private AllLambdasSerializableTransformer transformer;

	public InstrumentedClassLoaderRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected TestClass createTestClass(Class<?> testClass) {
		return super.createTestClass(instrumented(testClass));
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			setupTransformer();
			super.run(notifier);
		} finally {
			resetTransformer();
		}
	}

	private void resetTransformer() {
		try {
			if (transformer != null) {
				InstrumentationHolder inst = InstrumentationHolder.get();
				inst.removeTransformer(transformer);
				inst.retransformClasses(transformer.classesToRetransform());
			}
		} catch (ClassNotFoundException | UnmodifiableClassException e) {
			throw new RuntimeException(e);
		}
	}

	private void setupTransformer() {
		if (transformer != null) {
			try {
				if (!InstrumentationHolder.transformers.contains(transformer)) {
					InstrumentationHolder inst = InstrumentationHolder.get();
					inst.addTransformer(transformer, true);
					inst.retransformClasses(transformer.classesToRetransform());
				}
			} catch (ClassNotFoundException | UnmodifiableClassException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Class<?> instrumented(Class<?> klass) {
		try {
			Instrumented instrumented = klass.getAnnotation(Instrumented.class);
			loader = createLoader(klass, instrumented);
			if (instrumented != null && instrumented.serializeLambdas()) {
				transformer = AllLambdasSerializableTransformer.INSTANCE;
			}
			return loader.loadClass(klass.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private InstrumentedClassLoader createLoader(Class<?> klass, Instrumented instrumented) {
		try {
			TestRecorderAgentConfig config = instrumented == null ? new DefaultTestRecorderAgentConfig() : instrumented.config().newInstance();
			SnapshotInstrumentor instrumentor = new SnapshotInstrumentor(config);

			String[] classes = instrumented == null ? new String[0] : instrumented.classes();
			return new InstrumentedClassLoader(instrumentor, klass, classes);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
