package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.lang.instrument.Instrumentation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;

@ExtendWith(LoggerExtension.class)
public class TestRecorderAgentTest {

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	public void testInitialize(ExtensibleClassLoader loader, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.AgentInitializer".getBytes());
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null, loader);
		TestRecorderAgent agent = new TestRecorderAgent(Mockito.mock(Instrumentation.class), agentconfig);

		agent.initialize();

		assertThat(info.toString()).contains("init");
		assertThat(error.toString()).isEmpty();
	}

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	public void testInitializeWithInitializationFailure(ExtensibleClassLoader loader, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.BrokenAgentInitializer".getBytes());
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null, loader);
		TestRecorderAgent agent = new TestRecorderAgent(Mockito.mock(Instrumentation.class), agentconfig);

		agent.initialize();

		assertThat(info.toString()).contains("loading BrokenAgentInitializer");
		assertThat(error.toString()).contains("initializer BrokenAgentInitializer failed");
	}

}
