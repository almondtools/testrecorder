package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.profile.Methods;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {"java.lang.String"}, config = LiteralsTest.Profile.class)
public class LiteralsTest {

	@Test
	public void testCodeWithLiteralMethodRecorded() throws Exception {

		boolean contains = "string".contains("str");

		assertThat(contains).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(String.class)).hasSize(1);
		assertThat(testGenerator.renderTest(String.class).getTestCode()).contains("\"string\".contains(\"str\")");
		assertThat(testGenerator.renderTest(String.class)).satisfies(testsRun());
	}

	public static class Profile extends DefaultSerializationProfile {

		@Override
		public List<Methods> getRecorded() {
			return asList(Methods.byDescription(method()));
		}

		private Method method() {
			try {
				return String.class.getDeclaredMethod("contains", CharSequence.class);
			} catch (NoSuchMethodException | SecurityException e) {
				return null;
			}
		}
	}
}