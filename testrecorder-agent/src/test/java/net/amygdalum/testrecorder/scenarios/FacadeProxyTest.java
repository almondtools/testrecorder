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
	"net.amygdalum.testrecorder.scenarios.FacadeProxyContainer",
	"net.amygdalum.testrecorder.scenarios.FacadeInterfaceExample"
}, config = ThreadExcludingProfile.class)
public class FacadeProxyTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		FacadeProxyContainer container = new FacadeProxyContainer();

		container.writeToFacade("hello");
		container.writeToFacade("world");

		String hello = container.readFromFacade();
		String world = container.readFromFacade();

		assertThat(hello).isEqualTo("hello");
		assertThat(world).isEqualTo("world");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(FacadeProxyContainer.class)).hasSize(4);
		assertThat(testGenerator.renderTest(FacadeProxyContainer.class)).satisfies(testsRun());
	}

	@Test
	public void testInput() throws Exception {
		FacadeProxyContainer container = new FacadeProxyContainer("hello", "world");

		String hello = container.readFromFacade();
		String world = container.readFromFacade();

		assertThat(hello).isEqualTo("hello");
		assertThat(world).isEqualTo("world");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(FacadeProxyContainer.class)).hasSize(2);
		assertThat(testGenerator.testsFor(FacadeProxyContainer.class))
			.allSatisfy(test -> {
				assertThat(test)
					.doesNotContain("BufferedReader")
					.doesNotContain("BufferedWriter")
					.doesNotContain("PipedReader")
					.doesNotContain("PipedWriter")
					.containsWildcardPattern("Proxy.newProxyInstance(this.getClass().getClassLoader(),*new Class[]{*FacadeInterfaceExample.class*},*new PlaceHolderInvocationHandler())");
			});
	}

	@Test
	public void testOutput() throws Exception {
		FacadeProxyContainer container = new FacadeProxyContainer();

		container.writeToFacade("hello");
		container.writeToFacade("world");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(FacadeProxyContainer.class)).hasSize(2);
		assertThat(testGenerator.testsFor(FacadeProxyContainer.class))
			.allSatisfy(test -> {
				assertThat(test)
					.doesNotContain("BufferedReader")
					.doesNotContain("BufferedWriter")
					.doesNotContain("PipedReader")
					.doesNotContain("PipedWriter")
					.containsWildcardPattern("Proxy.newProxyInstance(this.getClass().getClassLoader(),*new Class[]{*FacadeInterfaceExample.class*},*new PlaceHolderInvocationHandler())");
			});
	}

}