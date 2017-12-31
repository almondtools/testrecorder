package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

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
		assertThat(testGenerator.renderTest(GenericConstructor.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(GenericConstructor.class), testsRun(CollectionDataTypes.class));
	}

}