package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.singleton;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

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
	public void testGenerateOnExcludedType() throws Exception {
		when(facade.excludes(Random.class)).thenReturn(true);
		SerializedValue value =  serializer.generate(Random.class, Random.class);

		assertThat(value, instanceOf(SerializedNull.class));
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

	@Test
	public void testPopulateOtherNullType() throws Exception {
		SerializedNull nullValue = SerializedNull.nullInstance(String.class);

		serializer.populate(nullValue, "Element" );

		assertThat(nullValue.getType(), equalTo(String.class));
	}

	@Test
	public void testPopulateOtherReferenceTypes() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class);

		serializer.populate(set, singleton("Element"));

		assertThat(set, empty());
	}

	@SuppressWarnings("unused")
	private static class GenericObject {
		private String stringField;
		private int intField;

		public GenericObject(String stringField, int intField) {
			this.stringField = stringField;
			this.intField = intField;
		}

	}

}
