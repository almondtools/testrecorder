package net.amygdalum.testrecorder.test;

import java.util.function.Consumer;

import org.assertj.core.api.SoftAssertions;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;
import net.amygdalum.testrecorder.util.Instantiations;

public class TestsRun implements Consumer<RenderedTest> {

	private SoftAssertions softly;
	private String[] code;
	private DynamicClassCompiler compiler;

	public TestsRun(String... code) {
		this.softly = new SoftAssertions();
		this.code = code;
		this.compiler = new DynamicClassCompiler();
	}

	public static TestsRun testsRun() {
		return new TestsRun();
	}

	public static TestsRun testsRunWith(String... code) {
		return new TestsRun(code);
	}

	@Override
	public void accept(RenderedTest test) {
		ClassLoader backupLoader = Thread.currentThread().getContextClassLoader();
		test:try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			for (String nextCode : code) {
				compiler.compile(nextCode, clazz.getClassLoader());
			}
			Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
			JUnitCore junit = new JUnitCore();
			Instantiations.resetInstatiations();
			Result result = junit.run(clazz);
			if (result.wasSuccessful()) {
				break test;
			}
			softly.fail("compiled successfully but got test failures : %s", result.getFailureCount());
			for (Failure failure : result.getFailures()) {
				Throwable exception = failure.getException();
				softly.fail("in %s: %s", failure.getTestHeader(), exception.getMessage());
			}
		} catch (DynamicClassCompilerException e) {
			softly.fail(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				softly.fail(msg);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(backupLoader);
		}
		softly.assertAll();
	}

}
