package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class BeanObjectAdaptorTest {

	private BeanObjectAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new BeanObjectAdaptor();
	}
	
	@Test
	public void testParentIsDefaultObject() throws Exception {
		assertThat(adaptor.parent(), sameInstance(DefaultObjectAdaptor.class));
	}

	@Test
	public void testMatchesAnyObject() throws Exception {
		assertThat(adaptor.matches(Object.class),is(true));
		assertThat(adaptor.matches(new Object(){}.getClass()),is(true));
	}

	@Test(expected=DeserializationException.class)
	public void testTryDeserializeWithNonBean() throws Exception {
		SerializedObject value = new SerializedObject(TestObject.class);
		value.addField(new SerializedField(String.class, "attribute", String.class, SerializedLiteral.literal("Hello World")));
		TypeManager types = new TypeManager();
		SetupGenerators generator = new SetupGenerators(new LocalVariableNameGenerator(), types);
		
		adaptor.tryDeserialize(value, generator);

	}
	
	@Test
	public void testTryDeserializeWithBean() throws Exception {
		SerializedObject value = new SerializedObject(TestBean.class);
		value.addField(new SerializedField(String.class, "attribute", String.class, literal("Hello World")));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("TestBean testBean1 = new TestBean()"), 
			containsString("testBean1.setAttribute(\"Hello World\")")));
		assertThat(result.getValue(), equalTo("testBean1"));
	}
	
	@SuppressWarnings("unused") 
	public static class TestBean {
		private String attribute;
		
		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}
		
	}

	@SuppressWarnings("unused") 
	public static class TestObject {
		private String attribute;
	}

}
