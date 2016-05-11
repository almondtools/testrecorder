package net.amygdalum.testrecorder.dynamiccompile;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class CompilableMatcher extends TypeSafeDiagnosingMatcher<String> {

	private DynamicClassCompiler compiler;

	public CompilableMatcher(ClassLoader loader) {
		compiler = new DynamicClassCompiler(loader);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should compile with success");
	}

	@Override
	protected boolean matchesSafely(String item, Description mismatchDescription) {
		try {
			Class<?> clazz = compiler.compile(item);
			return clazz != null;
		} catch (DynamicClassCompilerException e) {
			mismatchDescription.appendText(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				mismatchDescription.appendText("\n\t" + msg);
			}
			return false;
		}
	}

	public static CompilableMatcher compiles(ClassLoader loader) {
		return new CompilableMatcher(loader);
	}

	public static CompilableMatcher compiles(Class<?> clazz) {
		return compiles(clazz.getClassLoader());
	}

}
