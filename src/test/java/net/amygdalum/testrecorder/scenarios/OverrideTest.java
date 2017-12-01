package net.amygdalum.testrecorder.scenarios;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Overridden", "net.amygdalum.testrecorder.scenarios.Overriding" })
public class OverrideTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testOverridingRecordedMethodsReplacingSuperDoesNotRecord() throws Exception {
		Overriding o = new Overriding();
		int result = o.methodForReplacement(0l);

		assertThat(result, equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Overridden.class), empty());
	}

	@Test
	public void testOverridingRecordedMethodsCallingSuperDoesNotRecord() throws Exception {
		Overriding o = new Overriding();
		int result = o.methodForExtension(0l);

		assertThat(result, equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Overridden.class), empty());
	}
}