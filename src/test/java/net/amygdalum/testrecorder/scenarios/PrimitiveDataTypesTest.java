package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.PrimitiveDataTypes" })
public class PrimitiveDataTypesTest {

	@Test
	public void testCompilable() throws Exception {
		PrimitiveDataTypes dataTypes = new PrimitiveDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.booleans(i % 2 == 0);
			dataTypes.chars((char) i);
			dataTypes.bytes((byte) i);
			dataTypes.shorts((short) i);
			dataTypes.integers(i);
			dataTypes.floats((float) i);
			dataTypes.longs(i);
			dataTypes.doubles((double) i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testAsserts() throws Exception {
		PrimitiveDataTypes dataTypes = new PrimitiveDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.booleans(i % 2 == 0);
			dataTypes.chars((char) i);
			dataTypes.bytes((byte) i);
			dataTypes.shorts((short) i);
			dataTypes.integers(i);
			dataTypes.floats((float) i);
			dataTypes.longs(i);
			dataTypes.doubles((double) i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class).getTestCode())
			.doesNotContain("assertThat(false, equalTo(false))")
			.doesNotContain("assertThat(true, equalTo(true))")
			.doesNotContain("assertThat('\u0001', equalTo('\u0001'))")
			.doesNotContain("assertThat((byte) 1, equalTo((byte) 1))")
			.doesNotContain("assertThat((short) 1, equalTo((short) 1))")
			.doesNotContain("assertThat(1, equalTo(1))")
			.doesNotContain("assertThat(1.0f, equalTo(1.0f))")
			.doesNotContain("assertThat(1l, equalTo(1l))")
			.doesNotContain("assertThat(1.0, equalTo(1.0))");
	}
}