package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
	"net.amygdalum.testrecorder.scenarios.FacadeInterfaceContainer",
	"net.amygdalum.testrecorder.scenarios.FacadeInterfaceExample",
	"net.amygdalum.testrecorder.scenarios.FacadeImplementationExample"
}, config = ThreadExcludingProfile.class)
public class FacadeInterfaceTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		FacadeInterfaceContainer container = new FacadeInterfaceContainer();

		container.writeToFacade("hello");
		container.writeToFacade("world");

		String hello = container.readFromFacade();
		String world = container.readFromFacade();

		assertThat(hello).isEqualTo("hello");
		assertThat(world).isEqualTo("world");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(FacadeInterfaceContainer.class)).hasSize(4);
		assertThat(testGenerator.renderTest(FacadeInterfaceContainer.class)).satisfies(testsRun());
	}

	@Test
	public void testInput() throws Exception {
		FacadeInterfaceContainer container = new FacadeInterfaceContainer("hello", "world");

		String hello = container.readFromFacade();
		String world = container.readFromFacade();

		assertThat(hello).isEqualTo("hello");
		assertThat(world).isEqualTo("world");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(FacadeInterfaceContainer.class)).hasSize(2);
		assertThat(testGenerator.testsFor(FacadeInterfaceContainer.class))
			.allSatisfy(test -> {
				assertThat(test)
					.doesNotContain("BufferedReader")
					.doesNotContain("BufferedWriter")
					.doesNotContain("PipedReader")
					.doesNotContain("PipedWriter")
					.containsWildcardPattern("new GenericObject() {*}.as(FacadeImplementationExample.class)");
			});
	}

	@Test
	public void testOutput() throws Exception {
		FacadeInterfaceContainer container = new FacadeInterfaceContainer();

		container.writeToFacade("hello");
		container.writeToFacade("world");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(FacadeInterfaceContainer.class)).hasSize(2);
		assertThat(testGenerator.testsFor(FacadeInterfaceContainer.class))
			.allSatisfy(test -> {
				assertThat(test)
					.doesNotContain("BufferedReader")
					.doesNotContain("BufferedWriter")
					.doesNotContain("PipedReader")
					.doesNotContain("PipedWriter")
					.containsWildcardPattern("new GenericObject() {*}.as(FacadeImplementationExample.class)");
			});
	}

}