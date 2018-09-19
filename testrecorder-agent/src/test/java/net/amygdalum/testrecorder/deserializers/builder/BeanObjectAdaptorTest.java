package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedObject;

public class BeanObjectAdaptorTest {

	private AgentConfiguration config;
	private BeanObjectAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new BeanObjectAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentIsDefaultObject() throws Exception {
		assertThat(adaptor.parent()).isSameAs(DefaultObjectAdaptor.class);
	}

	@Test
	public void testMatchesAnyObject() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeWithNonBean() throws Exception {
		SerializedObject value = new SerializedObject(Simple.class);
		value.addField(new SerializedField(new FieldSignature(String.class, String.class, "attribute"), literal("Hello World")));
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithBean() throws Exception {
		SerializedObject value = new SerializedObject(Bean.class);
		value.addField(new SerializedField(new FieldSignature(String.class, String.class, "attribute"), literal("Hello World")));
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"Bean bean1 = new Bean()",
			"bean1.setAttribute(\"Hello World\")");
		assertThat(result.getValue()).isEqualTo("bean1");
	}

	private Deserializer generator() {
		return new SetupGenerators(new Adaptors().load(config.loadConfigurations(SetupGenerator.class))).newGenerator(context);
	}

}
