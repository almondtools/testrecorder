package net.amygdalum.testrecorder.testing.hamcrest;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.testing.hamcrest.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;

public class TestsRunnableMatcherTest {

	@Test
	public void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		testsRun().describeTo(description);

		assertThat(description.toString()).isEqualTo("should compile and run with success");
	}

	@Test
	public void testMatchesSafelyWithSuccess() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun().matchesSafely(new RenderedTest(TestsRunnableMatcherTest.class, ""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "  }"
			+ "}"), description);

		assertThat(matches).isTrue();
		assertThat(description.toString()).isEqualTo("");
	}

	@Test
	public void testMatchesSafelyWithTestFailure() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun().matchesSafely(new RenderedTest(TestsRunnableMatcherTest.class, ""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ "import static org.junit.Assert.fail;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    fail();"
			+ "  }"
			+ "}"), description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern(""
			+ "compiled successfully but got test failures : 1*"
			+ "-\tnull\n\tAssertionError: null");
	}

	@Test
	public void testMatchesSafelyWithCompileFailure() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun().matchesSafely(new RenderedTest(TestsRunnableMatcherTest.class, ""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ "import static org.junit.Assert.fail;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    fail() // missing semicolon"
			+ "  }"
			+ "}"), description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern(""
			+ "compile failed with messages");
	}

	@Test
	public void testMatchesSafelyWithError() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun().matchesSafely(new RenderedTest(TestsRunnableMatcherTest.class, ""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    throw new RuntimeException();"
			+ "  }"
			+ "}"), description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern(""
			+ "compiled successfully but got test failures : 1*"
			+ "-\tnull\n\tRuntimeException: null");
	}

}
