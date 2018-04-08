package net.amygdalum.testrecorder.test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertionError;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;

public class TestsFailTest {

	@Test
	public void testCompileError() throws Exception {
		assertThatThrownBy(() -> TestsFail.testsFail().accept(new RenderedTest(this.getClass(), "")))
			.isInstanceOf(SoftAssertionError.class)
			.hasMessageContaining("contains no public class");
	}

	@Test
	public void testDetailedCompileError() throws Exception {
		assertThatThrownBy(() -> TestsFail.testsFail().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "	public void testName() throws Exception {"
			+ "   int i = \"str\";"
			+ "	}"
			+ "}")))
				.isInstanceOf(SoftAssertionError.class)
				.hasMessageContaining("compile failed with messages")
				.hasMessageContaining("incompatible types");
	}

	@Test
	public void testTestError() throws Exception {
		assertThatCode(() -> TestsFail.testsFail().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "}")))
				.doesNotThrowAnyException();
	}

	@Test
	public void testTestSuccess() throws Exception {
		assertThatCode(() -> TestsFail.testsFail().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "	"
			+ " @org.junit.Test"
			+ " public void testName() throws Exception {"
			+ "	}"
			+ "}")))
				.isInstanceOf(SoftAssertionError.class)
				.hasMessageContaining("expected test failures but tests were successful");
	}

	@Test
	public void testRuntimeException() throws Exception {
		assertThatThrownBy(() -> TestsFail.testsFail().accept(new RenderedTest(this.getClass(), "") {
			public String getTestCode() {
				throw new RuntimeException();
			}
		}))
			.isInstanceOf(RuntimeException.class);
	}

}
