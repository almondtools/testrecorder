package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Annotations" })
public class AnnotationsTest {

	@Test
	public void testTimestampCompilesAndRuns() throws Exception {
		Annotations annotations = new Annotations("WITH_TIMESTAMP");

		int result = annotations.withTimeStamp("123");
		assertThat(result).isEqualTo(123);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Annotations.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Annotations.class)).satisfies(testsRun());
	}

	@Test
	public void testTimestampCode() throws Exception {
		Annotations annotations = new Annotations("WITH_TIMESTAMP");

		int result = annotations.withTimeStamp("123");
		assertThat(result).isEqualTo(123);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Annotations.class))
			.hasSize(1)
			.anySatisfy(test -> assertThat(test)
				.containsWildcardPattern("@AnnotatedBy(name = \"timestamp\", value = \"*\")")
				.containsWildcardPattern("new Annotations(\"WITH_TIMESTAMP\")")
				.contains("equalTo(123)"));
	}

	@Test
	public void testGroupCompilesAndRuns() throws Exception {
		Annotations annotations = new Annotations("WITH_GROUP");

		int result = annotations.withGroup("123");
		assertThat(result).isEqualTo(123);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Annotations.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Annotations.class)).satisfies(testsRun());
	}

	@Test
	public void testGroupCode() throws Exception {
		Annotations annotations = new Annotations("WITH_GROUP");

		int result = annotations.withGroup("123");
		assertThat(result).isEqualTo(123);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Annotations.class))
			.hasSize(1)
			.anySatisfy(test -> assertThat(test)
				.containsWildcardPattern("@AnnotatedBy(name = \"group\", value = \"WITH_GROUP\")")
				.containsWildcardPattern("new Annotations(\"WITH_GROUP\")")
				.contains("equalTo(123)"));
	}

		
		

}