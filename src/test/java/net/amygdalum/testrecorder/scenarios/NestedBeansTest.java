package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.util.testobjects.NestedAbstract;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.NestedBeans" })
public class NestedBeansTest {

	@Test
	public void testNestedPublicCompilesAndRuns() throws Exception {
		NestedAbstract bean = NestedAbstract.createNestedPublic();
		bean.setId(2);

		int extractedId = NestedBeans.extractId(bean);
		
		assertThat(extractedId).isEqualTo(2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedBeans.class)).satisfies(testsRun());
	}

	@Test
	public void testNestedPublicNonPublicConstructorCompilesAndRuns() throws Exception {
		NestedAbstract bean = NestedAbstract.createNestedPublicNonPublicConstructor();
		bean.setId(2);

		int extractedId = NestedBeans.extractId(bean);
		
		assertThat(extractedId).isEqualTo(2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedBeans.class)).satisfies(testsRun());
	}

	@Test
	public void testNestedPackagePrivatePublicConstructorCompilesAndRuns() throws Exception {
		NestedAbstract bean = NestedAbstract.createNestedPackagePrivatePublicConstructor();
		bean.setId(2);
		
		int extractedId = NestedBeans.extractId(bean);
		
		assertThat(extractedId).isEqualTo(2);
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedBeans.class)).satisfies(testsRun());
	}
	
}