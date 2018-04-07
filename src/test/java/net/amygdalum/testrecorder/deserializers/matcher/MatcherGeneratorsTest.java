package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Collections.arrayList;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.createCompletelyHidden;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.createPartiallyHidden;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.SerializedValues;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.ContainingList;
import net.amygdalum.testrecorder.util.testobjects.Dubble;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MatcherGeneratorsTest {

	private AgentConfiguration config;
	private SerializedValues values;
	private MatcherGenerators matcherCode;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		values = new SerializedValues(config);
		matcherCode = new MatcherGenerators(config);
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testNullIsSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(nullInstance(Object.class))).isTrue();
		assertThat(matcherCode.simpleMatcher(nullInstance(Object.class), context).getStatements()).isEmpty();
		assertThat(matcherCode.simpleMatcher(nullInstance(Object.class), context).getValue()).isEqualTo("null");
	}

	@Test
	public void testLiteralIsSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(literal("str"))).isTrue();
		assertThat(matcherCode.simpleMatcher(literal("str"), context).getStatements()).isEmpty();
		assertThat(matcherCode.simpleMatcher(literal("str"), context).getValue()).isEqualTo("\"str\"");
	}

	@Test
	public void testOtherIsNotSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(values.object(Dubble.class, new Dubble("Foo", "Bar")))).isFalse();
		assertThat(matcherCode.simpleMatcher(values.object(Dubble.class, new Dubble("Foo", "Bar")), context).getStatements()).isEmpty();
		assertThat(matcherCode.simpleMatcher(values.object(Dubble.class, new Dubble("Foo", "Bar")), context).getValue()).containsWildcardPattern("new GenericMatcher() {*"
			+ "a = \"Foo\"*"
			+ "b = \"Bar\"*"
			+ "}.matching(Dubble.class)");
	}

	@Test
	public void testVisitField() throws Exception {

		Type type = parameterized(ArrayList.class, null, String.class);
		SerializedList value = values.list(type, arrayList("Foo", "Bar"));

		Computation result = matcherCode.visitField(new SerializedField(ContainingList.class, "list", type, value), context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("Matcher<?> list = containsInOrder(String.class, \"Foo\", \"Bar\");");
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));

		Computation result = matcherCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*"
			+ "a = \"Foo\"*"
			+ "b = \"Bar\"*"
			+ "}.matching(Dubble.class)");
	}

	@Test
	public void testVisitReferenceTypePartiallyHidden() throws Exception {
		VisibleInterface o = createPartiallyHidden();
		SerializedObject value = values.object(Hidden.VisibleInterface.class, o);

		Computation result = matcherCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*"
			+ "}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$PartiallyHidden\"), VisibleInterface.class)");
	}

	@Test
	public void testVisitReferenceTypeCompletelyHidden() throws Exception {
		Object o = createCompletelyHidden();
		SerializedObject value = values.object(o.getClass(), o);

		Computation result = matcherCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*}"
			+ ".matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\"))");
	}

	@Test
	public void testVisitReferenceTypeComputedPartiallyHidden() throws Exception {
		VisibleInterface o = createPartiallyHidden();
		SerializedObject value = values.object(Hidden.VisibleInterface.class, o);
		Computation result = matcherCode.visitReferenceType(value, context);

		result = matcherCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("recursive(VisibleInterface.class)");
	}

	@Test
	public void testVisitReferenceTypeComputedCompletelyHidden() throws Exception {
		Object o = createCompletelyHidden();
		SerializedObject value = values.object(o.getClass(), o);
		Computation result = matcherCode.visitReferenceType(value, context);

		result = matcherCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("recursive(Object.class)");
	}

	@Test
	public void testVisitReferenceTypeCheckSkipped() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));

		Computation result = matcherCode.visitReferenceType(value, context.newWithHints(skipChecks()));

		assertThat(result).isNull();
	}

	@Test
	public void testVisitReferenceTypeRevisited() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));
		matcherCode.visitReferenceType(value, context);

		Computation result = matcherCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("recursive(Dubble.class)");
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));

		Computation result = matcherCode.visitImmutableType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("equalTo(new BigInteger(\"42\"))");
	}

	@Test
	public void testVisitImmutableTypeCheckSkipped() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));

		Computation result = matcherCode.visitImmutableType(value, context.newWithHints(skipChecks()));

		assertThat(result).isNull();
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("equalTo(42)");
	}

	@Test
	public void testVisitValueTypeCheckSkipped() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value, context.newWithHints(skipChecks()));

		assertThat(result).isNull();
	}

	@Test
	public void testSimpleMatcherSerializedValueNull() throws Exception {
		Computation result = matcherCode.simpleMatcher(SerializedNull.nullInstance(String.class), context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).as("generic matchers can match nulls and do not need matchers here").isEqualTo("null");
	}

	@Test
	public void testSimpleMatcherSerializedValueLiteral() throws Exception {
		Computation result = matcherCode.simpleMatcher(SerializedLiteral.literal("string"), context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).as("generic matchers can match literals and do not need matchers here").isEqualTo("\"string\"");
	}

	@Test
	public void testSimpleMatcherSerializedValueObject() throws Exception {
		Computation result = matcherCode.simpleMatcher(values.object(Simple.class, new Simple()), context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*String str = null;*}.matching(Simple.class)");
	}

	@Test
	public void testTemporaryLocal() throws Exception {
		assertThat(context.temporaryLocal()).isEqualTo("temp1");
		assertThat(context.temporaryLocal()).isEqualTo("temp2");
	}

	@Test
	public void testNewLocal() throws Exception {
		assertThat(context.newLocal("var")).isEqualTo("var1");
		assertThat(context.newLocal("var")).isEqualTo("var2");
	}

	private Object[] skipChecks() {
		return new Object[] { new SkipChecks() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return SkipChecks.class;
			}
		} };
	}

}
