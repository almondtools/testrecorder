package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.deserializers.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.runtime.GenericObjectException;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedEnum;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class SimpleDeserializerTest {

	private SimpleDeserializer deserializer;

	@Before
	public void before() throws Exception {
		deserializer = new SimpleDeserializer();
	}

	@Test(expected = DeserializationException.class)
	public void testVisitField() throws Exception {
		SerializedField field = new SerializedField(Simple.class, "field", String.class, literal("v"));
		deserializer.visitField(field, NULL);
	}

	@Test
	public void testVisitObject() throws Exception {
		SerializedObject object = new SerializedObject(Simple.class);
		object.addField(new SerializedField(Simple.class, "str", String.class, literal("v")));

		Object visitReferenceType = deserializer.visitReferenceType(object, NULL);

		assertThat(visitReferenceType, instanceOf(Simple.class));
		assertThat(((Simple) visitReferenceType).getStr(), equalTo("v"));
	}

	@Test(expected = DeserializationException.class)
	public void testVisitObjectNoDeserializable() throws Exception {
		SerializedObject object = Mockito.mock(SerializedObject.class);
		when(object.getFields()).thenThrow(new GenericObjectException());

		deserializer.visitReferenceType(object, NULL);
	}

	@Test
	public void testVisitArray() throws Exception {
		SerializedArray object = new SerializedArray(int[].class);
		object.add(literal(22));

		Object visitReferenceType = deserializer.visitReferenceType(object, NULL);

		assertThat(visitReferenceType, equalTo(new int[] { 22 }));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testVisitList() throws Exception {
		SerializedList object = new SerializedList(ArrayList.class);
		object.add(literal(1));

		Object visitReferenceType = deserializer.visitReferenceType(object, NULL);

		assertThat(visitReferenceType, instanceOf(ArrayList.class));
		assertThat(((List) visitReferenceType).get(0), equalTo(1));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testVisitSet() throws Exception {
		SerializedSet object = new SerializedSet(HashSet.class);
		object.add(literal(true));

		Object visitReferenceType = deserializer.visitReferenceType(object, NULL);

		assertThat(visitReferenceType, instanceOf(HashSet.class));
		assertThat(((Set) visitReferenceType).iterator().next(), equalTo(true));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testVisitMap() throws Exception {
		SerializedMap object = new SerializedMap(HashMap.class);
		object.put(literal(1.0), literal("one"));

		Object visitReferenceType = deserializer.visitReferenceType(object, NULL);

		assertThat(visitReferenceType, instanceOf(HashMap.class));
		assertThat(((Map) visitReferenceType).get(1.0), equalTo("one"));
	}

	@Test
	public void testVisitNull() throws Exception {
		SerializedReferenceType object = SerializedNull.nullInstance(Object.class);
		assertThat(deserializer.visitReferenceType(object, NULL), nullValue());
	}

	@Test
	public void testVisitOtherReferenceType() throws Exception {
		SerializedReferenceType object = Mockito.mock(SerializedReferenceType.class);
		assertThat(deserializer.visitReferenceType(object, NULL), nullValue());
	}

	@Test
	public void testVisitImmutable() throws Exception {
		SerializedImmutable<BigDecimal> serializedImmutable = new SerializedImmutable<>(BigDecimal.class);
		serializedImmutable.setValue(new BigDecimal("2.0"));

		Object visitImmutableType = deserializer.visitImmutableType(serializedImmutable, NULL);

		assertThat(visitImmutableType, equalTo(new BigDecimal("2.0")));
	}

	@Test
	public void testVisitEnum() throws Exception {
		SerializedEnum serializedEnum = new SerializedEnum(TestEnum.class);
		serializedEnum.setName("ENUM");

		Object visitImmutableType = deserializer.visitImmutableType(serializedEnum, NULL);

		assertThat(visitImmutableType, equalTo(TestEnum.ENUM));
	}

	@Test
	public void testVisitOtherImmutable() throws Exception {
		SerializedImmutableType serializedImmutable = Mockito.mock(SerializedImmutableType.class);

		Object visitImmutableType = deserializer.visitImmutableType(serializedImmutable, NULL);

		assertThat(visitImmutableType, nullValue());
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral serializedLiteral = SerializedLiteral.literal('a');

		Object visitImmutableType = deserializer.visitValueType(serializedLiteral, NULL);

		assertThat(visitImmutableType, equalTo('a'));
	}

	@Test
	public void testVisitValueTypeCached() throws Exception {
		SerializedLiteral serializedLiteral = SerializedLiteral.literal('a');

		Object visitImmutableType = deserializer.visitValueType(serializedLiteral, NULL);
		visitImmutableType = deserializer.visitValueType(serializedLiteral, NULL);

		assertThat(visitImmutableType, equalTo('a'));
	}

	public static enum TestEnum {
		ENUM;
	}

}
