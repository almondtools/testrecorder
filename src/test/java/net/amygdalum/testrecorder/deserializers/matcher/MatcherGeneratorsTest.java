package net.amygdalum.testrecorder.deserializers.matcher;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Collections.arrayList;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.createCompletelyHidden;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.createPartiallyHidden;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.util.testobjects.ContainingList;
import net.amygdalum.testrecorder.util.testobjects.Dubble;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface;
import net.amygdalum.testrecorder.util.testobjects.SerializedValues;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MatcherGeneratorsTest {

	private SerializedValues values;
	private MatcherGenerators matcherCode;

	@BeforeEach
	public void before() throws Exception {
		values = new SerializedValues();
		matcherCode = new MatcherGenerators(getClass());
	}

	@Test
	public void testNullIsSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(nullInstance(Object.class)), is(true));
		assertThat(matcherCode.simpleMatcher(nullInstance(Object.class), NULL).getStatements(), empty());
		assertThat(matcherCode.simpleMatcher(nullInstance(Object.class), NULL).getValue(), equalTo("null"));
	}

	@Test
	public void testLiteralIsSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(literal("str")), is(true));
		assertThat(matcherCode.simpleMatcher(literal("str"), NULL).getStatements(), empty());
		assertThat(matcherCode.simpleMatcher(literal("str"), NULL).getValue(), equalTo("\"str\""));
	}

	@Test
	public void testOtherIsNotSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(values.object(Dubble.class, new Dubble("Foo", "Bar"))), is(false));
		assertThat(matcherCode.simpleMatcher(values.object(Dubble.class, new Dubble("Foo", "Bar")), NULL).getStatements(), empty());
		assertThat(matcherCode.simpleMatcher(values.object(Dubble.class, new Dubble("Foo", "Bar")), NULL).getValue(), containsPattern("new GenericMatcher() {*"
			+ "a = \"Foo\"*"
			+ "b = \"Bar\"*"
			+ "}.matching(Dubble.class)"));
	}

	@Test
	public void testVisitField() throws Exception {

		Type type = parameterized(ArrayList.class, null, String.class);
		SerializedList value = values.list(type, arrayList("Foo", "Bar"));

		Computation result = matcherCode.visitField(new SerializedField(ContainingList.class, "list", type, value), NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("Matcher<?> list = containsInOrder(String.class, \"Foo\", \"Bar\");"));
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));

		Computation result = matcherCode.visitReferenceType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("new GenericMatcher() {*"
			+ "a = \"Foo\"*"
			+ "b = \"Bar\"*"
			+ "}.matching(Dubble.class)"));
	}

	@Test
	public void testVisitReferenceTypePartiallyHidden() throws Exception {
		VisibleInterface o = createPartiallyHidden();
		SerializedObject value = values.object(Hidden.VisibleInterface.class, o);

		Computation result = matcherCode.visitReferenceType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("new GenericMatcher() {*"
			+ "}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$PartiallyHidden\"), VisibleInterface.class)"));
	}

	@Test
	public void testVisitReferenceTypeCompletelyHidden() throws Exception {
		Object o = createCompletelyHidden();
		SerializedObject value = values.object(o.getClass(), o);

		Computation result = matcherCode.visitReferenceType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("new GenericMatcher() {*}"
			+ ".matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\"))"));
	}

	@Test
	public void testVisitReferenceTypeComputedPartiallyHidden() throws Exception {
		VisibleInterface o = createPartiallyHidden();
		SerializedObject value = values.object(Hidden.VisibleInterface.class, o);
		Computation result = matcherCode.visitReferenceType(value, NULL);

		result = matcherCode.visitReferenceType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("recursive(VisibleInterface.class)"));
	}

	@Test
	public void testVisitReferenceTypeComputedCompletelyHidden() throws Exception {
		Object o = createCompletelyHidden();
		SerializedObject value = values.object(o.getClass(), o);
		Computation result = matcherCode.visitReferenceType(value, NULL);

		result = matcherCode.visitReferenceType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("recursive(Object.class)"));
	}

	@Test
	public void testVisitReferenceTypeCheckSkipped() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));

		Computation result = matcherCode.visitReferenceType(value, NULL.newWithHints(skipChecks()));

		assertThat(result, nullValue());
	}

	@Test
	public void testVisitReferenceTypeRevisited() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));
		matcherCode.visitReferenceType(value, NULL);

		Computation result = matcherCode.visitReferenceType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("recursive(Dubble.class)"));
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));

		Computation result = matcherCode.visitImmutableType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("equalTo(new BigInteger(\"42\"))"));
	}

	@Test
	public void testVisitImmutableTypeCheckSkipped() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));

		Computation result = matcherCode.visitImmutableType(value, NULL.newWithHints(skipChecks()));

		assertThat(result, nullValue());
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("equalTo(42)"));
	}

	@Test
	public void testVisitValueTypeCheckSkipped() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value, NULL.newWithHints(skipChecks()));

		assertThat(result, nullValue());
	}

	@Test
	public void testSimpleMatcherSerializedValueNull() throws Exception {
		Computation result = matcherCode.simpleMatcher(SerializedNull.nullInstance(String.class), NULL);

		assertThat(result.getStatements(), empty());
		assertThat("generic matchers can match nulls and do not need matchers here", result.getValue(), equalTo("null"));
	}

	@Test
	public void testSimpleMatcherSerializedValueLiteral() throws Exception {
		Computation result = matcherCode.simpleMatcher(SerializedLiteral.literal("string"), NULL);

		assertThat(result.getStatements(), empty());
		assertThat("generic matchers can match literals and do not need matchers here", result.getValue(), equalTo("\"string\""));
	}

	@Test
	public void testSimpleMatcherSerializedValueObject() throws Exception {
		Computation result = matcherCode.simpleMatcher(values.object(Simple.class, new Simple()), NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("new GenericMatcher() {*String str = null;*}.matching(Simple.class)"));
	}

	@Test
	public void testTemporaryLocal() throws Exception {
		assertThat(matcherCode.temporaryLocal(), equalTo("temp1"));
		assertThat(matcherCode.temporaryLocal(), equalTo("temp2"));
	}

	@Test
	public void testNewLocal() throws Exception {
		assertThat(matcherCode.newLocal("var"), equalTo("var1"));
		assertThat(matcherCode.newLocal("var"), equalTo("var2"));
	}

	private Object[] skipChecks() {
		return new Object[] { new SkipChecks() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return SkipChecks.class;
			}
		}};
	}

}
