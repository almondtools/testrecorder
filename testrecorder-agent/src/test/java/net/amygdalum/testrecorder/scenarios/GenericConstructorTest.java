package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GenericConstructor" })
public class GenericConstructorTest {

	
	
	@Test
	public void testArrayListCompilable() throws Exception {
		List<String> list = new ArrayList<>(asList("A","B"));

		GenericConstructor object = new GenericConstructor(list);
		for (String value : asList("C","D")) {
			object.add(value);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericConstructor.class)).hasSize(2);
		assertThat(testGenerator.renderTest(GenericConstructor.class)).satisfies(testsRun());
	}

}