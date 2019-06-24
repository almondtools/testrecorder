package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive;
import net.amygdalum.testrecorder.profile.DefaultConfigNoArguments;
import net.amygdalum.testrecorder.profile.OtherConfigNoArguments;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class TestRecorderAgentTest {

	@Test
	public void testLoadConfig(TemporaryFolder folder) throws Exception {
		Path folder1 = folder.provideFolder("agentconfig").toAbsolutePath();
		Files.write(folder1.resolve(ConfigNoArgumentsNonExclusive.class.getName()), DefaultConfigNoArguments.class.getName().getBytes(), StandardOpenOption.CREATE);

		Path folder2 = folder.provideFolder("otherconfig").toAbsolutePath();
		Files.write(folder2.resolve(ConfigNoArgumentsNonExclusive.class.getName()), OtherConfigNoArguments.class.getName().getBytes(), StandardOpenOption.CREATE);

		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(folder1 + ";" + folder2);

		assertThat(agentconfig.loadConfigurations(ConfigNoArgumentsNonExclusive.class)).hasSize(2);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testPrepareInstrumentations() throws Exception {
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		Instrumentation inst = Mockito.mock(Instrumentation.class);
		TestRecorderAgent agent = new TestRecorderAgent(inst, agentconfig, asList(ClassFileTransformer1.class, ClassFileTransformer2.class));

		agent.prepareInstrumentations();

		ArgumentCaptor<AttachableClassFileTransformer> argument = ArgumentCaptor.forClass(AttachableClassFileTransformer.class);
		verify(inst, Mockito.atLeastOnce()).addTransformer(argument.capture(), anyBoolean());
		assertThat(argument.getAllValues())
			.extracting(object -> (Class) object.getClass())
			.containsExactlyInAnyOrder(ClassFileTransformer1.class, ClassFileTransformer2.class);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testClearInstrumentations() throws Exception {
		AgentConfiguration agentconfig = TestRecorderAgent.loadConfig(null);
		Instrumentation inst = Mockito.mock(Instrumentation.class);
		TestRecorderAgent agent = new TestRecorderAgent(inst, agentconfig, asList(ClassFileTransformer1.class, ClassFileTransformer2.class));
		agent.prepareInstrumentations();

		agent.clearInstrumentations();

		ArgumentCaptor<AttachableClassFileTransformer> argument = ArgumentCaptor.forClass(AttachableClassFileTransformer.class);
		verify(inst, Mockito.atLeastOnce()).removeTransformer(argument.capture());
		assertThat(argument.getAllValues())
			.extracting(object -> (Class) object.getClass())
			.containsExactlyInAnyOrder(ClassFileTransformer2.class, ClassFileTransformer1.class);
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

	public static class ClassFileTransformer1 extends AttachableClassFileTransformer {

		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
			return null;
		}

		@Override
		public Collection<Class<?>> filterClassesToRetransform(Class<?>[] loaded) {
			return emptyList();
		}

		@Override
		public Collection<Class<?>> getClassesToRetransform() {
			return emptyList();
		}

	}

	public static class ClassFileTransformer2 extends AttachableClassFileTransformer {

		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
			return null;
		}

		@Override
		public Collection<Class<?>> filterClassesToRetransform(Class<?>[] loaded) {
			return emptyList();
		}

		@Override
		public Collection<Class<?>> getClassesToRetransform() {
			return emptyList();
		}

	}
}
