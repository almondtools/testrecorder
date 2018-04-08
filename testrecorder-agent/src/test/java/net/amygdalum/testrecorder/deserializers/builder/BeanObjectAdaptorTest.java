package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class BeanObjectAdaptorTest {

	private AgentConfiguration config;
	private BeanObjectAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new BeanObjectAdaptor();
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
		value.addField(new SerializedField(String.class, "attribute", String.class, SerializedLiteral.literal("Hello World")));
		TypeManager types = new DeserializerTypeManager();
		SetupGenerators generator = new SetupGenerators(config);

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator, new DefaultDeserializerContext(types, new LocalVariableNameGenerator())));
	}

	@Test
	public void testTryDeserializeWithBean() throws Exception {
		SerializedObject value = new SerializedObject(Bean.class);
		value.addField(new SerializedField(String.class, "attribute", String.class, literal("Hello World")));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, new DefaultDeserializerContext());

		assertThat(result.getStatements().toString()).containsSequence(
			"Bean bean1 = new Bean()",
			"bean1.setAttribute(\"Hello World\")");
		assertThat(result.getValue()).isEqualTo("bean1");
	}

}
