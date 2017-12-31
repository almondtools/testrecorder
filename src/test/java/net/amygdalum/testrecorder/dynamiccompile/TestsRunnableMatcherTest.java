package net.amygdalum.testrecorder.dynamiccompile;

import static net.amygdalum.assertjconventions.Assertions.assertThat;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class TestsRunnableMatcherTest {

	@Test
	public void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		testsRun(TestsRunnableMatcherTest.class).describeTo(description);

		assertThat(description.toString()).isEqualTo("should compile and run with success");
	}

	@Test
	public void testMatchesSafelyWithSuccess() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "  }"
			+ "}", description);

		assertThat(matches).isTrue();
		assertThat(description.toString()).isEqualTo("");
	}

	@Test
	public void testMatchesSafelyWithTestFailure() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ "import static org.junit.Assert.fail;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    fail();"
			+ "  }"
			+ "}", description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern(""
			+ "compiled successfully but got test failures : 1*"
			+ "-\tnull\n\tAssertionError: null");
	}

	@Test
	public void testMatchesSafelyWithCompileFailure() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ "import static org.junit.Assert.fail;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    fail() // missing semicolon"
			+ "  }"
			+ "}", description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern(""
			+ "compile failed with messages");
	}

	@Test
	public void testMatchesSafelyWithError() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.Test;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    throw new RuntimeException();"
			+ "  }"
			+ "}", description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern(""
			+ "compiled successfully but got test failures : 1*"
			+ "-\tnull\n\tRuntimeException: null");
	}

}
