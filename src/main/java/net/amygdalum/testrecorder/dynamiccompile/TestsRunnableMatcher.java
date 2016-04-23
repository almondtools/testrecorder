package net.amygdalum.testrecorder.dynamiccompile;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestsRunnableMatcher extends TypeSafeDiagnosingMatcher<String> {

	private DynamicClassCompiler compiler;

	public TestsRunnableMatcher() {
		compiler = new DynamicClassCompiler();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should compile and run with success");
	}
	
	@Override
	protected boolean matchesSafely(String item, Description mismatchDescription) {
		try {
			Class<?> clazz = compiler.compile(item);
			JUnitCore junit = new JUnitCore();
			Result result = junit.run(clazz);
			if (result.wasSuccessful()) {
				return true;
			}
			mismatchDescription.appendText("compiled successfully but got test failures : " + result.getFailureCount());
			for (Failure failure : result.getFailures()) {
				String message = failure.getMessage();
				mismatchDescription.appendText("\n- " + message);
			}
			return false;
		} catch (DynamicClassCompilerException e) {
			mismatchDescription.appendText(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				mismatchDescription.appendText("\n\t" + msg);
			}
			return false;
		}
	}

	public static TestsRunnableMatcher testsRuns() {
		return new TestsRunnableMatcher();
	}

}
