package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.singleton;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

	private SerializerSession session;
	private Serializer<SerializedReferenceType> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new GenericSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	void testGenerate() throws Exception {
		SerializedReferenceType value = serializer.generate(GenericObject.class, session);
		value.useAs(GenericObject.class);

		assertThat(value.getUsedTypes()).containsExactly(GenericObject.class);
		assertThat(value.getType()).isEqualTo(GenericObject.class);
	}

	@Test
	void testGenerateOnExcludedType() throws Exception {
		when(session.excludes(Random.class)).thenReturn(true);
		SerializedReferenceType value = serializer.generate(Random.class, session);
		value.useAs(Random.class);

		assertThat(value).isInstanceOf(SerializedNull.class);
	}

	@Test
	void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal(int.class, 1);
		when(session.excludes(any(Field.class))).thenAnswer(field -> ((Field) field.getArguments()[0]).isSynthetic());
		when(session.find("Foo")).thenReturn(foo);
		when(session.find("Bar")).thenReturn(bar);
		SerializedObject value = (SerializedObject) serializer.generate(GenericObject.class, session);
		value.useAs(GenericObject.class);

		serializer.populate(value, new GenericObject("Foo", 1), session);

		SerializedField fooField = new SerializedField(GenericObject.class, "stringField", String.class, foo);
		SerializedField barField = new SerializedField(GenericObject.class, "intField", int.class, bar);
		assertThat(value.getFields()).containsExactlyInAnyOrder(fooField, barField);
	}

	@Test
	void testPopulateOtherNullType() throws Exception {
		SerializedNull nullValue = SerializedNull.nullInstance();

		serializer.populate(nullValue, "Element", session);

		assertThat(nullValue).isEqualTo(SerializedNull.nullInstance());
	}

	@Test
	void testPopulateOtherReferenceTypes() throws Exception {
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
