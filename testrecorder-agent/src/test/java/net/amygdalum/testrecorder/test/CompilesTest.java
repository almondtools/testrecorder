package net.amygdalum.testrecorder.test;

import static net.amygdalum.testrecorder.test.Compiles.compiles;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

import net.amygdalum.testrecorder.generator.RenderedTest;

public class CompilesTest {

	@Test
	public void testCompileError() throws Exception {
		assertThatThrownBy(() -> compiles().accept(new RenderedTest(this.getClass(), "")))
			.isInstanceOf(MultipleFailuresError.class)
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
				.isInstanceOf(MultipleFailuresError.class)
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
