package net.amygdalum.testrecorder.testing.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;

public class CompilableMatcher extends TypeSafeDiagnosingMatcher<RenderedTest> {

	private DynamicClassCompiler compiler;

	public CompilableMatcher() {
		compiler = new DynamicClassCompiler();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should compile with success");
	}

	@Override
	protected boolean matchesSafely(RenderedTest test, Description mismatchDescription) {
		try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			return clazz != null;
		} catch (DynamicClassCompilerException e) {
			mismatchDescription.appendText(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				mismatchDescription.appendText("\n\t" + msg);
			}
			return false;
		}
	}

	public static CompilableMatcher compiles() {
		return new CompilableMatcher();
	}

}
