package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.GenericDataTypes"})
public class GenericDataTypesTest {
	
	@Test
	public void testIntegerCompilable() throws Exception {
		StringBuilder buffer = new StringBuilder();
		
		GenericDataTypes<Integer> dataTypes = new GenericDataTypes<>();
		for (int i = 1; i <= 10; i++) {
			dataTypes.objects(buffer, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testStringCompilable() throws Exception {
		StringBuilder buffer = new StringBuilder();
		
		GenericDataTypes<String> dataTypes = new GenericDataTypes<>();
		for (int i = 1; i <= 10; i++) {
			dataTypes.objects(buffer, "string " + i);
		}
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericDataTypes.class)).satisfies(testsRun());
	}
}