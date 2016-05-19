package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

@RunWith(MockitoJUnitRunner.class)
public class GenericSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedReferenceType> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new GenericSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), empty());
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedObject value = (SerializedObject) serializer.generate(GenericObject.class, GenericObject.class);

		assertThat(value.getResultType(), equalTo(GenericObject.class));
		assertThat(value.getType(), equalTo(GenericObject.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedField fooField = new SerializedField(GenericObject.class, "stringField", String.class, foo);
		SerializedValue bar = literal(int.class, 1);
		SerializedField barField = new SerializedField(GenericObject.class, "intField", int.class, bar);
		when(facade.serialize(eq(GenericObject.class.getDeclaredField("stringField")), any())).thenReturn(fooField);
		when(facade.serialize(eq(GenericObject.class.getDeclaredField("intField")), any())).thenReturn(barField);
		SerializedObject value = (SerializedObject) serializer.generate(GenericObject.class, GenericObject.class);

		serializer.populate(value, new GenericObject("Foo", 1));

		assertThat(value.getFields(), containsInAnyOrder(fooField, barField));
	}

	@SuppressWarnings("unused")
	private static class GenericObject {
		private String stringField;
		private int intField;
		
		public GenericObject(String stringField,int intField) {
			this.stringField = stringField;
			this.intField = intField;
		}
		
	}
	
}
