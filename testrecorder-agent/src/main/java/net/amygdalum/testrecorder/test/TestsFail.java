package net.amygdalum.testrecorder.test;

import java.util.function.Consumer;

import org.assertj.core.api.SoftAssertions;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;
import net.amygdalum.testrecorder.util.Instantiations;

public class TestsFail implements Consumer<RenderedTest> {

	private SoftAssertions softly;
	private DynamicClassCompiler compiler;

	public TestsFail() {
		softly = new SoftAssertions();
		compiler = new DynamicClassCompiler();
	}

	public static TestsFail testsFail() {
		return new TestsFail();
	}

	@Override
	public void accept(RenderedTest test) {
		ClassLoader backupLoader = Thread.currentThread().getContextClassLoader();
		try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
			JUnitCore junit = new JUnitCore();
			Instantiations.resetInstatiations();
			Result result = junit.run(clazz);
			if (result.wasSuccessful()) {
				softly.fail("expected test failures but tests were successful");
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
