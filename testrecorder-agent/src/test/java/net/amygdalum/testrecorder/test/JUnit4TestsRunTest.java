package net.amygdalum.testrecorder.test;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

import net.amygdalum.testrecorder.generator.RenderedTest;

public class JUnit4TestsRunTest {

	@Test
	void testCompileError() throws Exception {
		assertThatThrownBy(() -> testsRun().accept(new RenderedTest(this.getClass(), "")))
			.isInstanceOf(MultipleFailuresError.class)
			.hasMessageContaining("contains no public class");
	}

	@Test
	void testDetailedCompileError() throws Exception {
		assertThatThrownBy(() -> testsRun().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "	public void testName() throws Exception {"
			+ "   int i = \"str\";"
			+ "	}"
			+ "}")))
				.isInstanceOf(MultipleFailuresError.class)
				.hasMessageContaining("compile failed with messages")
				.hasMessageContaining("incompatible types");
	}

	@Test
	void testTestError() throws Exception {
		assertThatThrownBy(() -> testsRun().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "	"
			+ " @org.junit.Test"
			+ " public void testFails() throws Exception {"
			+ "   assert false : \"failed assertion\";"
			+ "	}"
			+ "}")))
				.isInstanceOf(MultipleFailuresError.class)
				.hasMessageContaining("compiled successfully")
				.hasMessageContaining("failed assertion");
	}

	@Test
	void testTestSuccess() throws Exception {
		assertThatCode(() -> testsRun().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "	"
			+ " @org.junit.Test"
			+ " public void testSuccess() throws Exception {"
			+ "	}"
			+ "}")))
				.doesNotThrowAnyException();
	}

	@Test
	void testRuntimeException() throws Exception {
		assertThatThrownBy(() -> testsRun().accept(new RenderedTest(this.getClass(), "") {
			public String getTestCode() {
				throw new RuntimeException();
			}
		}))
			.isInstanceOf(RuntimeException.class);
	}

}
