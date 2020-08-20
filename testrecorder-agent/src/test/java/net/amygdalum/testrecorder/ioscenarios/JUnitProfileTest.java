package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestAgentConfiguration;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.test.JUnit4TestsRun;
import net.amygdalum.testrecorder.test.JUnit5TestsRun;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {"net.amygdalum.testrecorder.ioscenarios.Inputs"})
public class JUnitProfileTest {

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	void testJUnit4Code(TestAgentConfiguration config, ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.generator.JUnit4TestGeneratorProfile".getBytes());
		config.init().withLoader(loader);
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		testGenerator.reload(config);

		Inputs in = new Inputs();
		in.recorded();

		testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Inputs.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Inputs.class).getTestCode())
			.contains("import org.junit.Before;")
			.contains("import org.junit.After;")
			.contains("import org.junit.Test;");
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(JUnit4TestsRun.testsRun());
	}

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	void testJUnit5Code(TestAgentConfiguration config, ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.generator.JUnit5TestGeneratorProfile".getBytes());
		config.init().withLoader(loader);
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		testGenerator.reload(config);

		Inputs in = new Inputs();
		in.recorded();

		testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Inputs.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Inputs.class).getTestCode())
			.contains("import org.junit.jupiter.api.Test;")
			.contains("import org.junit.jupiter.api.BeforeEach;")
			.contains("import org.junit.jupiter.api.AfterEach;");
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(JUnit5TestsRun.testsRun());
	}

}