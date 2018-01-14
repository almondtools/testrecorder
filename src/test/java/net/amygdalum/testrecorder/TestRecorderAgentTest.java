package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.lang.instrument.Instrumentation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.scenarios.ScenarioAgentConfig;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.ServiceLoaderExtension;

@ExtendWith(LoggerExtension.class)
public class TestRecorderAgentTest {

	@Test
	public void testLoadConfigLoading(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		TestRecorderAgentConfig config = TestRecorderAgent.loadConfig("net.amygdalum.testrecorder.scenarios.ScenarioAgentConfig");
		assertThat(config).isInstanceOf(ScenarioAgentConfig.class);
		assertThat(info.toString()).contains("loading config ScenarioAgentConfig");
	}

	@Test
	public void testLoadConfigFailing(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		TestRecorderAgentConfig config = TestRecorderAgent.loadConfig("net.amygdalum.testrecorder.NotExistingTestRecorderAgentConfig");
		assertThat(config).isInstanceOf(DefaultTestRecorderAgentConfig.class);
		assertThat(error.toString()).contains("failed loading config net.amygdalum.testrecorder.NotExistingTestRecorderAgentConfig");
		assertThat(info.toString()).contains("loading default config");
	}

	@Test
	@ExtendWith(ServiceLoaderExtension.class)
	public void testInitialize(ExtensibleClassLoader loader, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		loader.defineResource("META-INF/services/net.amygdalum.testrecorder.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.AgentInitializer".getBytes());

		TestRecorderAgent agent = new TestRecorderAgent(Mockito.mock(Instrumentation.class));

		agent.initialize(new DefaultTestRecorderAgentConfig());

		assertThat(info.toString()).contains("init");
		assertThat(error.toString()).isEmpty();
	}

	@Test
	@ExtendWith(ServiceLoaderExtension.class)
	public void testInitializeWithInitializationFailure(ExtensibleClassLoader loader, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		loader.defineResource("META-INF/services/net.amygdalum.testrecorder.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.BrokenAgentInitializer".getBytes());

		TestRecorderAgent agent = new TestRecorderAgent(Mockito.mock(Instrumentation.class));

		agent.initialize(new DefaultTestRecorderAgentConfig());

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("initializer BrokenAgentInitializer failed");
	}

}
