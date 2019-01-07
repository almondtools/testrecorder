package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestAgentConfiguration;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.generator.JUnit4TestTemplate;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.generator.TestGeneratorProfile;
import net.amygdalum.testrecorder.generator.TestTemplate;
import net.amygdalum.testrecorder.hints.Setter;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {"net.amygdalum.testrecorder.scenarios.CustomConstructed"})
public class CustomConstructedTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		CustomConstructed bean = new CustomConstructed();
		bean.string("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CustomConstructed.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		CustomConstructed bean = new CustomConstructed();
		bean.string("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.testsFor(CustomConstructed.class))
			.anySatisfy(test -> {
				assertThat(test)
					.contains("new CustomConstructed()")
					.containsWildcardPattern("customConstructed?.string")
					.contains("equalTo(3)");
			});
	}

	@Test
	public void testCodeGeneric() throws Exception {
		CustomConstructed bean = new CustomConstructed();
		bean.other("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.testsFor(CustomConstructed.class))
			.anySatisfy(test -> {
				assertThat(test)
					.doesNotContain("new CustomConstructed()")
					.containsWildcardPattern("new GenericObject() {*other = \"str\";*}")
					.contains("equalTo(3)");
			});
	}
	
	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	public void testCustomCodeWithSetterHint(TestAgentConfiguration config, ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.scenarios.CustomConstructedTest$CustomTestGeneratorProfile".getBytes());
		config.reset().withLoader(loader);
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		testGenerator.reload(config);

		CustomConstructed bean = new CustomConstructed();
		bean.string("string");
		bean.other("other");

		assertThat(bean.hashCode()).isEqualTo(11);

		testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.testsFor(CustomConstructed.class))
			.anySatisfy(test -> {
				assertThat(test)
					.contains("new CustomConstructed()")
					.containsWildcardPattern("customConstructed?.string")
					.containsWildcardPattern("customConstructed?.other")
					.contains("equalTo(11)");
			});
		assertThat(testGenerator.renderTest(CustomConstructed.class).getTestCode())
			.contains("import org.junit.Test;");
		assertThat(testGenerator.renderTest(CustomConstructed.class)).satisfies(testsRun());
	}

	public static class CustomTestGeneratorProfile implements TestGeneratorProfile {

		@Override
		public List<CustomAnnotation> annotations() {
			try {
				return asList(new CustomAnnotation(CustomConstructed.class.getDeclaredMethod("other", String.class), new Setter() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return Setter.class;
					}
				}));
			} catch (ReflectiveOperationException e) {
				return emptyList();
			}
		}

		@Override
		public Class<? extends TestTemplate> template() {
			return JUnit4TestTemplate.class;
		}

	}
}