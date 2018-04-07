package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsFail.testsFail;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestAgentConfiguration;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Globals" })
public class InitializerTest {

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	public void testWithInitializer(TestAgentConfiguration config, ExtensibleClassLoader loader) throws Exception {
		loader.addPackage("net.amygdalum.testrecorder.scenarios");
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.scenarios.GlobalsInitializer".getBytes());
		config.reset().withLoader(loader);

		Globals.global = 0;
		new GlobalsInitializer().run();

		Globals.incGlobal();

		assertThat(Globals.getSum()).isEqualTo(43);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Globals.class)).hasSize(2);
		assertThat(testGenerator.renderTest(Globals.class)).satisfies(testsRun());
	}

	@Test
	public void testWithoutInitializer() throws Exception {
		Globals.global = 0;
		new GlobalsInitializer().run();

		Globals.incGlobal();

		assertThat(Globals.getSum()).isEqualTo(43);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Globals.class)).hasSize(2);
		assertThat(testGenerator.renderTest(Globals.class)).satisfies(testsFail());
	}

}
