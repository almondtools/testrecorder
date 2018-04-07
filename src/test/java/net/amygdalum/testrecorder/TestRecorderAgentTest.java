package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayOutputStream;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ConfigNoArguments;
import net.amygdalum.testrecorder.profile.DefaultConfigNoArguments;
import net.amygdalum.testrecorder.profile.OtherConfigNoArguments;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class TestRecorderAgentTest {

	@Test
	public void testLoadConfig(TemporaryFolder folder) throws Exception {
		Path folder1 = folder.provideFolder("agentconfig").toAbsolutePath();
		Files.write(folder1.resolve(ConfigNoArguments.class.getName()), DefaultConfigNoArguments.class.getName().getBytes(), StandardOpenOption.CREATE);

		Path folder2 = folder.provideFolder("otherconfig").toAbsolutePath();
		Files.write(folder2.resolve(ConfigNoArguments.class.getName()), OtherConfigNoArguments.class.getName().getBytes(), StandardOpenOption.CREATE);

		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(folder1 + ";" + folder2);

		assertThat(agentconfig.loadConfigurations(ConfigNoArguments.class)).hasSize(2);
	}

	@ExtendWith(LoggerExtension.class)
	@ExtendWith(ClasspathResourceExtension.class)
	@Test
	public void testInitialize(ExtensibleClassLoader loader, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.AgentInitializer".getBytes());
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		TestRecorderAgent agent = new TestRecorderAgent(Mockito.mock(Instrumentation.class), agentconfig);

		agent.initialize();

		assertThat(info.toString()).contains("init");
		assertThat(error.toString()).isEmpty();
	}

	@ExtendWith(LoggerExtension.class)
	@ExtendWith(ClasspathResourceExtension.class)
	@Test
	public void testInitializeWithInitializationFailure(ExtensibleClassLoader loader, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.BrokenAgentInitializer".getBytes());
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		TestRecorderAgent agent = new TestRecorderAgent(Mockito.mock(Instrumentation.class), agentconfig);

		agent.initialize();

		assertThat(info.toString()).contains("loading BrokenAgentInitializer");
		assertThat(error.toString()).contains("initializer BrokenAgentInitializer failed");
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testPrepareInstrumentations() throws Exception {
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		Instrumentation inst = Mockito.mock(Instrumentation.class);
		TestRecorderAgent agent = Mockito.spy(new TestRecorderAgent(inst, agentconfig));

		agent.prepareInstrumentations();

		ArgumentCaptor<AttachableClassFileTransformer> argument = ArgumentCaptor.forClass(AttachableClassFileTransformer.class);
		verify(inst, Mockito.atLeastOnce()).addTransformer(argument.capture(), anyBoolean());
		assertThat(argument.getAllValues())
			.extracting(object -> (Class) object.getClass())
			.containsExactlyInAnyOrder(SnapshotInstrumentor.class, AllLambdasSerializableTransformer.class);
		verify(agent).initialize();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testClearInstrumentations() throws Exception {
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		Instrumentation inst = Mockito.mock(Instrumentation.class);
		TestRecorderAgent agent = new TestRecorderAgent(inst, agentconfig);
		agent.prepareInstrumentations();

		agent.clearInstrumentations();

		ArgumentCaptor<AttachableClassFileTransformer> argument = ArgumentCaptor.forClass(AttachableClassFileTransformer.class);
		verify(inst, Mockito.atLeastOnce()).removeTransformer(argument.capture());
		assertThat(argument.getAllValues())
			.extracting(object -> (Class) object.getClass())
			.containsExactlyInAnyOrder(SnapshotInstrumentor.class, AllLambdasSerializableTransformer.class);
	}

	@Test
	public void testClearInstrumentationsOnUnprepared() throws Exception {
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		Instrumentation inst = Mockito.mock(Instrumentation.class);
		TestRecorderAgent agent = new TestRecorderAgent(inst, agentconfig);

		agent.clearInstrumentations();

		verifyNoMoreInteractions(inst);
	}

	@Test
	public void testGetConfig() throws Exception {
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		Instrumentation inst = Mockito.mock(Instrumentation.class);
		TestRecorderAgent agent = new TestRecorderAgent(inst, agentconfig);

		assertThat(agent.getConfig()).isSameAs(agentconfig);
	}

}
