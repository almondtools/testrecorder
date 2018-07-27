package net.amygdalum.testrecorder.test;

import static net.amygdalum.testrecorder.test.Compiles.compiles;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertionError;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;

public class CompilesTest {

	@Test
	public void testCompileError() throws Exception {
		assertThatThrownBy(() -> compiles().accept(new RenderedTest(this.getClass(), "")))
			.isInstanceOf(SoftAssertionError.class)
			.hasMessageContaining("contains no public class");
	}

	@Test
	public void testDetailedCompileError() throws Exception {
		assertThatThrownBy(() -> compiles().accept(new RenderedTest(this.getClass(), ""
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
	public void testCompileSuccess() throws Exception {
		assertThatCode(() -> compiles().accept(new RenderedTest(this.getClass(), ""
			+ "package net.amygdalum.testrecorder.testing.assertj;"
			+ "public class Test {"
			+ "}")))
				.doesNotThrowAnyException();
	}

}
