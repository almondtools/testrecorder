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
@Instrumented(classes = { 
	"net.amygdalum.testrecorder.scenarios.SpecialFieldObject", 
	"net.amygdalum.testrecorder.scenarios.SpecialInterface", 
	}, config = ProxyIncludingProfile.class)
public class SpecialFieldObjectTest {

	@Test
	public void testCode() throws Exception {
		SpecialFieldObject bean = new SpecialFieldObject();
		bean.init();

		assertThat(bean.method()).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SpecialFieldObject.class).getTestCode())
			.contains("Proxy.newProxyInstance");
	}

	@Test
	public void testCompilesAndRuns() throws Exception {
		SpecialFieldObject bean = new SpecialFieldObject();
		bean.init();

		assertThat(bean.method()).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SpecialFieldObject.class)).satisfies(testsRun());
	}
}