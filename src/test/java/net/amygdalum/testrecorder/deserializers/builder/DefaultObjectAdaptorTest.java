package net.amygdalum.testrecorder.deserializers.builder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultObjectAdaptorTest {

	private DefaultObjectAdaptor adaptor;

	@Before
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
		SerializedObject value = new SerializedObject(TestObject.class);
		value.addField(new SerializedField(String.class, "attribute", String.class, SerializedLiteral.literal("Hello World")));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("TestObject testObject1 = new GenericObject"),
			containsString("String attribute = \"Hello World\""),
			containsString("as(TestObject.class)")));
		assertThat(result.getValue(), equalTo("testObject1"));
	}
	
	@SuppressWarnings("unused") 
	public static class TestObject {
		private String attribute;
	}

}
