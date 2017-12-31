package net.amygdalum.testrecorder.dynamiccompile;

import static net.amygdalum.assertjconventions.Assertions.assertThat;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static org.assertj.core.api.Assertions.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class CompilableMatcherTest {

	@Test
	public void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		compiles(CompilableMatcherTest.class).describeTo(description);

		assertThat(description.toString()).isEqualTo("should compile with success");
	}

	@Test
	public void testMatchesSafelyWithSuccess() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.jupiter.api.Test;"
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
	public void testMatchesSafelyCached() throws Exception {
		StringDescription description = new StringDescription();

		CompilableMatcher matcher = compiles(CompilableMatcherTest.class);

		boolean matches = matcher.matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.jupiter.api.Test;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "  }"
			+ "}", description);
		matches = matcher.matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.jupiter.api.Test;"
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
	public void testMatchesSafelyCompilesWithFailingTest() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.jupiter.api.Test;"
			+ "import static org.junit.Assert.fail;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    fail();"
			+ "  }"
			+ "}", description);

		assertThat(matches).isTrue();
	}

	@Test
	public void testMatchesSafelyWithCompileFailure() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.jupiter.api.Test;"
			+ "import static org.junit.Assert.fail;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    fail() // missing semicolon"
			+ "  }"
			+ "}", description);

		assertThat(matches).isFalse();
		assertThat(description.toString()).containsWildcardPattern("compile failed with messages");
	}

	@Test
	public void testMatchesSafelyCompilesWithTestError() throws Exception {
		StringDescription description = new StringDescription();

		boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
			+ "package net.amygdalum.testrecorder.dynamiccompile;"
			+ "import org.junit.jupiter.api.Test;"
			+ ""
			+ "public class SuccessTest {"
			+ "  @Test"
			+ "  public void test() throws Exception {"
			+ "    throw new RuntimeException();"
			+ "  }"
			+ "}", description);

		assertThat(matches).isTrue();
	}

}
