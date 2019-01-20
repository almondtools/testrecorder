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
import net.amygdalum.testrecorder.TestRecorderAgent;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.profile.Methods;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {"java.lang.String", "java.lang.StringBuffer"}, config = LiteralsTest.Profile.class)
public class LiteralsTest {

	@Test
	public void testCodeWithLiteralMethodRecorded(TestRecorderAgent agent) throws Exception {

		boolean contains = "string".contains("str");

		assertThat(contains).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();

		assertThat(testGenerator.testsFor(String.class)).hasSize(1);
		assertThat(testGenerator.renderTest(String.class).getTestCode()).contains("\"string\".contains(\"str\")");
		assertThat(testGenerator.renderTest(String.class)).satisfies(testsRun());
	}

	@Test
	public void testCodeWithLiteralMethodNoResultRecorded() throws Exception {
		char[] buffer = new char[6];
		buffer[0] = 'S';

		new StringBuffer("string").getChars(1, 6, buffer, 1);

		assertThat(new String(buffer)).isEqualTo("String");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(StringBuffer.class)).hasSize(1);
		assertThat(testGenerator.renderTest(StringBuffer.class).getTestCode()).containsWildcardPattern(".getChars(1, 6, *, 1)");
		assertThat(testGenerator.renderTest(StringBuffer.class)).satisfies(testsRun());
	}

	public static class Profile extends DefaultSerializationProfile {

		@Override
		public List<Methods> getRecorded() {
			return asList(Methods.byDescription(contains()), Methods.byDescription(getChars()));
		}

		private Method contains() {
			try {
				return String.class.getDeclaredMethod("contains", CharSequence.class);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		private Method getChars() {
			try {
				return StringBuffer.class.getDeclaredMethod("getChars", int.class, int.class, char[].class, int.class);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}
}