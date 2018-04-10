package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.singleton;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class GenericSerializerTest {

	private SerializerFacade facade;
	private SerializerSession session;
	private Serializer<SerializedReferenceType> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		session = mock(SerializerSession.class);
		serializer = new GenericSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedReferenceType value = serializer.generate(GenericObject.class, session);
		value.useAs(GenericObject.class);

		assertThat(value.getUsedTypes()).containsExactly(GenericObject.class);
		assertThat(value.getType()).isEqualTo(GenericObject.class);
	}

	@Test
	public void testGenerateOnExcludedType() throws Exception {
		when(facade.excludes(Random.class)).thenReturn(true);
		SerializedReferenceType value = serializer.generate(Random.class, session);
		value.useAs(Random.class);

		assertThat(value).isInstanceOf(SerializedNull.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedField fooField = new SerializedField(GenericObject.class, "stringField", String.class, foo);
		SerializedValue bar = literal(int.class, 1);
		SerializedField barField = new SerializedField(GenericObject.class, "intField", int.class, bar);
		when(facade.excludes(any(Field.class))).thenAnswer(field -> ((Field) field.getArguments()[0]).isSynthetic());
		when(facade.serialize(eq(GenericObject.class.getDeclaredField("stringField")), any(), any(SerializerSession.class))).thenReturn(fooField);
		when(facade.serialize(eq(GenericObject.class.getDeclaredField("intField")), any(), any(SerializerSession.class))).thenReturn(barField);
		SerializedObject value = (SerializedObject) serializer.generate(GenericObject.class, session);
		value.useAs(GenericObject.class);

		serializer.populate(value, new GenericObject("Foo", 1), session);

		assertThat(value.getFields()).containsExactlyInAnyOrder(fooField, barField);
	}

	@Test
	public void testPopulateOtherNullType() throws Exception {
		SerializedNull nullValue = SerializedNull.nullInstance(String.class);

		serializer.populate(nullValue, "Element", session);

		assertThat(nullValue.getType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulateOtherReferenceTypes() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class);

		serializer.populate(set, singleton("Element"), session);

		assertThat(set).isEmpty();
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
