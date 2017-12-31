package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAnyObject() throws Exception {
		assertThat(adaptor.matches(Object.class),is(true));
		assertThat(adaptor.matches(new Object(){}.getClass()),is(true));
	}

	@Test
	public void testTryDeserializeWithNonBean() throws Exception {
		SerializedObject value = new SerializedObject(Simple.class);
		value.addField(new SerializedField(String.class, "str", String.class, SerializedLiteral.literal("Hello World")));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("Simple simple1 = new GenericObject"),
			containsString("String str = \"Hello World\""),
			containsString("as(Simple.class)")));
		assertThat(result.getValue()).isEqualTo("simple1");
	}
	
}
