package net.amygdalum.testrecorder.values;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class ValuePrinterTest {

	private ValuePrinter printer;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		printer = new ValuePrinter();
		context = Mockito.mock(DeserializerContext.class);
	}

	@Test
	public void testVisitField() throws Exception {
		SerializedField field = new SerializedField(Simple.class, "field", String.class, literal("v"));

		assertThat(printer.visitField(field, context)).isEqualTo("java.lang.String field: v");
	}

	@Test
	public void testVisitObject() throws Exception {
		SerializedObject object = new SerializedObject(Simple.class);
		object.addField(new SerializedField(Simple.class, "str", String.class, literal("v")));

		String visitReferenceType = printer.visitReferenceType(object, context);

		assertThat(visitReferenceType).containsWildcardPattern(""
			+ "net.amygdalum.testrecorder.util.testobjects.Simple/*{*"
			+ "java.lang.String str: v*"
			+ "}");
	}

	@Test
	public void testVisitObjectCached() throws Exception {
		SerializedObject object = new SerializedObject(Simple.class);
		object.addField(new SerializedField(Simple.class, "str", String.class, literal("v")));

		String visitReferenceType = printer.visitReferenceType(object, context);
		visitReferenceType = printer.visitReferenceType(object, context);

		assertThat(visitReferenceType).containsWildcardPattern("net.amygdalum.testrecorder.util.testobjects.Simple/*");
		assertThat(visitReferenceType).doesNotContain("java.lang.String str: v");
	}

	@Test
	public void testVisitArray() throws Exception {
		SerializedArray object = new SerializedArray(int[].class);
		object.add(literal(22));

		String visitReferenceType = printer.visitReferenceType(object, context);

		assertThat(visitReferenceType).isEqualTo("<22>");
	}

	@Test
	public void testVisitList() throws Exception {
		SerializedList object = new SerializedList(ArrayList.class);
		object.add(literal(1));

		String visitReferenceType = printer.visitReferenceType(object, context);

		assertThat(visitReferenceType).isEqualTo("[1]");
	}

	@Test
	public void testVisitSet() throws Exception {
		SerializedSet object = new SerializedSet(HashSet.class);
		object.add(literal(true));

		String visitReferenceType = printer.visitReferenceType(object, context);

		assertThat(visitReferenceType).isEqualTo("{true}");
	}

	@Test
	public void testVisitMap() throws Exception {
		SerializedMap object = new SerializedMap(HashMap.class);
		object.put(literal(1.0), literal("one"));

		String visitReferenceType = printer.visitReferenceType(object, context);

		assertThat(visitReferenceType).isEqualTo("{1.0:one}");
	}

	@Test
	public void testVisitNull() throws Exception {
		SerializedReferenceType object = SerializedNull.nullInstance(Object.class);
		assertThat(printer.visitReferenceType(object, context)).isEqualTo("null");
	}

	@Test
	public void testVisitOtherReferenceType() throws Exception {
		SerializedReferenceType object = Mockito.mock(SerializedReferenceType.class);
		assertThat(printer.visitReferenceType(object, context)).isEqualTo("");
	}

	@Test
	public void testVisitImmutable() throws Exception {
		SerializedImmutable<BigDecimal> serializedImmutable = new SerializedImmutable<>(BigDecimal.class);
		serializedImmutable.setValue(new BigDecimal("2.0"));

		String visitImmutableType = printer.visitImmutableType(serializedImmutable, context);

		assertThat(visitImmutableType).isEqualTo("2.0");
	}

	@Test
	public void testVisitEnum() throws Exception {
		SerializedEnum serializedEnum = new SerializedEnum(TestEnum.class);
		serializedEnum.setName("ENUM");

		String visitImmutableType = printer.visitImmutableType(serializedEnum, context);

		assertThat(visitImmutableType).isEqualTo("ENUM");
	}

	@Test
	public void testVisitOtherImmutable() throws Exception {
		SerializedImmutableType serializedImmutable = Mockito.mock(SerializedImmutableType.class);

		String visitImmutableType = printer.visitImmutableType(serializedImmutable, context);

		assertThat(visitImmutableType).isEqualTo("");
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral serializedLiteral = SerializedLiteral.literal('a');

		String visitImmutableType = printer.visitValueType(serializedLiteral, context);

		assertThat(visitImmutableType).isEqualTo("a");
	}

	@Test
	public void testVisitValueTypeCached() throws Exception {
		SerializedLiteral serializedLiteral = SerializedLiteral.literal('a');

		String visitImmutableType = printer.visitValueType(serializedLiteral, context);
		visitImmutableType = printer.visitValueType(serializedLiteral, context);

		assertThat(visitImmutableType).isEqualTo("a");
	}

	public static enum TestEnum {
		ENUM;
	}

}
