package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultObjectAdaptorTest {

	private DefaultObjectAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultObjectAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
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
		value.addField(new SerializedField(String.class, "str", String.class, SerializedLiteral.literal("Hello World")));
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"Simple simple1 = new GenericObject",
			"String str = \"Hello World\"",
			"as(Simple.class)");
		assertThat(result.getValue()).isEqualTo("simple1");
	}

}
