package net.amygdalum.testrecorder.testing.assertj;

import java.util.function.Consumer;

import org.assertj.core.api.SoftAssertions;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import net.amygdalum.testrecorder.RenderedTest;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.util.Instantiations;

public class TestsRun implements Consumer<RenderedTest> {

	private SoftAssertions softly;
	private DynamicClassCompiler compiler;

	public TestsRun() {
		softly = new SoftAssertions();
		compiler = new DynamicClassCompiler();
	}

	public static TestsRun testsRun() {
		return new TestsRun();
	}

	@Override
	public void accept(RenderedTest test) {
		try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			JUnitCore junit = new JUnitCore();
			Instantiations.resetInstatiations();
			Result result = junit.run(clazz);
			if (result.wasSuccessful()) {
				return;
			}
			softly.fail("compiled successfully but got test failures : %s", result.getFailureCount());
			for (Failure failure : result.getFailures()) {
				Throwable exception = failure.getException();
				softly.fail("in " + failure.getTestHeader() + ": ", exception);
			}
		} catch (DynamicClassCompilerException e) {
			softly.fail(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				softly.fail(msg);
			}
		}
		softly.assertAll();
	}

}
