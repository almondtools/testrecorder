package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.runtime.GenericObjectException;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedEnum;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class SimpleDeserializerTest {

	private SimpleDeserializer deserializer;

	@BeforeEach
	public void before() throws Exception {
		deserializer = new SimpleDeserializer(new DefaultDeserializerContext());
	}

	@Test
	public void testVisitField() throws Exception {
		SerializedField field = new SerializedField(new FieldSignature(Simple.class, String.class, "field"), literal("v"));
		assertThatThrownBy(() -> deserializer.visitField(field)).isInstanceOf(DeserializationException.class);
	}

	@Test
	public void testVisitObject() throws Exception {
		SerializedObject object = new SerializedObject(Simple.class);
		object.addField(new SerializedField(new FieldSignature(Simple.class, String.class, "str"), literal("v")));

		Object visitReferenceType = deserializer.visitReferenceType(object);

		assertThat(visitReferenceType).isInstanceOf(Simple.class);
		assertThat(((Simple) visitReferenceType).getStr()).isEqualTo("v");
	}

	@Test
	public void testVisitObjectNoDeserializable() throws Exception {
		SerializedObject object = Mockito.mock(SerializedObject.class);
		when(object.getFields()).thenThrow(new GenericObjectException());

		assertThatThrownBy(() -> deserializer.visitReferenceType(object)).isInstanceOf(DeserializationException.class);
	}

	@Test
	public void testVisitArray() throws Exception {
		SerializedArray object = new SerializedArray(int[].class);
		object.add(literal(22));

		Object visitReferenceType = deserializer.visitReferenceType(object);

		assertThat(visitReferenceType).isEqualTo(new int[] { 22 });
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testVisitList() throws Exception {
		SerializedList object = new SerializedList(ArrayList.class);
		object.add(literal(1));

		Object visitReferenceType = deserializer.visitReferenceType(object);

		assertThat(visitReferenceType).isInstanceOf(ArrayList.class);
		assertThat(((List) visitReferenceType).get(0)).isEqualTo(1);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testVisitSet() throws Exception {
		SerializedSet object = new SerializedSet(HashSet.class);
		object.add(literal(true));

		Object visitReferenceType = deserializer.visitReferenceType(object);

		assertThat(visitReferenceType).isInstanceOf(HashSet.class);
		assertThat(((Set) visitReferenceType).iterator().next()).isEqualTo(true);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testVisitMap() throws Exception {
		SerializedMap object = new SerializedMap(HashMap.class);
		object.put(literal(1.0), literal("one"));

		Object visitReferenceType = deserializer.visitReferenceType(object);

		assertThat(visitReferenceType).isInstanceOf(HashMap.class);
		assertThat(((Map) visitReferenceType).get(1.0)).isEqualTo("one");
	}

	@Test
	public void testVisitNull() throws Exception {
		SerializedReferenceType object = nullInstance();
		assertThat(deserializer.visitReferenceType(object)).isNull();
	}

	@Test
	public void testVisitOtherReferenceType() throws Exception {
		SerializedReferenceType object = Mockito.mock(SerializedReferenceType.class);
		assertThat(deserializer.visitReferenceType(object)).isNull();
	}

	@Test
	public void testVisitImmutable() throws Exception {
		SerializedImmutable<BigDecimal> serializedImmutable = new SerializedImmutable<>(BigDecimal.class);
		serializedImmutable.setValue(new BigDecimal("2.0"));

		Object visitImmutableType = deserializer.visitImmutableType(serializedImmutable);

		assertThat(visitImmutableType).isEqualTo(new BigDecimal("2.0"));
	}

	@Test
	public void testVisitEnum() throws Exception {
		SerializedEnum serializedEnum = new SerializedEnum(TestEnum.class);
		serializedEnum.setName("ENUM");

		Object visitImmutableType = deserializer.visitImmutableType(serializedEnum);

		assertThat(visitImmutableType).isEqualTo(TestEnum.ENUM);
	}

	@Test
	public void testVisitOtherImmutable() throws Exception {
		SerializedImmutableType serializedImmutable = Mockito.mock(SerializedImmutableType.class);

		Object visitImmutableType = deserializer.visitImmutableType(serializedImmutable);

		assertThat(visitImmutableType).isNull();
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral serializedLiteral = literal('a');

		Object visitImmutableType = deserializer.visitValueType(serializedLiteral);

		assertThat(visitImmutableType).isEqualTo('a');
	}

	@Test
	public void testVisitValueTypeCached() throws Exception {
		SerializedLiteral serializedLiteral = literal('a');

		Object visitImmutableType = deserializer.visitValueType(serializedLiteral);
		visitImmutableType = deserializer.visitValueType(serializedLiteral);

		assertThat(visitImmutableType).isEqualTo('a');
	}

	public enum TestEnum {
		ENUM;
	}

}
