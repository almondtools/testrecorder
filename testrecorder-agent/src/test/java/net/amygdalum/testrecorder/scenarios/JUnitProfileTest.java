package net.amygdalum.testrecorder.scenarios;

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
@Instrumented(classes = {"net.amygdalum.testrecorder.scenarios.CustomConstructed"})
public class JUnitProfileTest {

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	void testJUnit4Code(TestAgentConfiguration config, ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.generator.JUnit4TestGeneratorProfile".getBytes());
		config.reset().withLoader(loader);
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		testGenerator.reload(config);

		CustomConstructed bean = new CustomConstructed();
		bean.string("string");
		bean.other("other");

		assertThat(bean.hashCode()).isEqualTo(11);

		testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CustomConstructed.class).getTestCode())
			.contains("import org.junit.Test;");
		assertThat(testGenerator.renderTest(CustomConstructed.class)).satisfies(JUnit4TestsRun.testsRun());
	}

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	void testJUnit5Code(TestAgentConfiguration config, ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.generator.JUnit5TestGeneratorProfile".getBytes());
		config.reset().withLoader(loader);
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		testGenerator.reload(config);

		CustomConstructed bean = new CustomConstructed();
		bean.string("string");
		bean.other("other");

		assertThat(bean.hashCode()).isEqualTo(11);

		testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CustomConstructed.class).getTestCode())
			.contains("import org.junit.jupiter.api.Test;");
		assertThat(testGenerator.renderTest(CustomConstructed.class)).satisfies(JUnit5TestsRun.testsRun());
	}

}