package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.profile.Fields;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GlobalFields" }, config=GlobalFieldsTest.Profile.class)
public class GlobalFieldsTest {

	@Test
	void testCompilableSettingFromNull() throws Exception {
		GlobalFields.global = null;

		GlobalFields.setGlobal("str");

		assertThat(GlobalFields.global).isEqualTo("str");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GlobalFields.class)).satisfies(testsRun());
	}

	@Test
	void testCompilableSettingToNull() throws Exception {
		GlobalFields.global = "str";

		GlobalFields.setGlobal(null);

		assertThat(GlobalFields.global).isNull();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GlobalFields.class)).satisfies(testsRun());
	}

	public static class Profile extends DefaultSerializationProfile {
		
		@Override
		public List<Fields> getGlobalFields() {
			return asList(Fields.byName("global"));
		}
	}
}