package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Collections.arrayList;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.createCompletelyHidden;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.createPartiallyHidden;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.SerializedValues;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.util.testobjects.ContainingList;
import net.amygdalum.testrecorder.util.testobjects.Dubble;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MatcherGeneratorsTest {

	private SerializedValues values;
	private Deserializer matcherCode;
	private DeserializerContext context;

	@BeforeEach
	void before() throws Exception {
		AgentConfiguration config = defaultConfig();
		values = new SerializedValues(config);

		context = new DefaultDeserializerContext();
		matcherCode = new MatcherGenerators(config).newGenerator(context);
	}

	@Test
	void testVisitField() throws Exception {

		Type type = parameterized(ArrayList.class, null, String.class);
		SerializedList value = values.list(type, arrayList("Foo", "Bar"));

		Computation result = matcherCode.visitField(new SerializedField(ContainingList.class, "list", type, value));

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("Matcher<?> list = containsInOrder(String.class, \"Foo\", \"Bar\");");
	}

	@Test
	void testVisitReferenceType() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*"
			+ "a = \"Foo\"*"
			+ "b = \"Bar\"*"
			+ "}.matching(Dubble.class)");
	}

	@Test
	void testVisitReferenceTypePartiallyHidden() throws Exception {
		VisibleInterface o = createPartiallyHidden();
		SerializedObject value = values.object(Hidden.VisibleInterface.class, o);

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*"
			+ "}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$PartiallyHidden\"), VisibleInterface.class)");
	}

	@Test
	void testVisitReferenceTypeCompletelyHidden() throws Exception {
		Object o = createCompletelyHidden();
		SerializedObject value = values.object(o.getClass(), o);

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*}"
			+ ".matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\"))");
	}

	@Test
	void testVisitReferenceTypeComputedPartiallyHidden() throws Exception {
		VisibleInterface o = createPartiallyHidden();
		SerializedObject value = values.object(Hidden.VisibleInterface.class, o);
		Computation result = matcherCode.visitReferenceType(value);

		result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("recursive(VisibleInterface.class)");
	}

	@Test
	void testVisitReferenceTypeComputedCompletelyHidden() throws Exception {
		Object o = createCompletelyHidden();
		SerializedObject value = values.object(o.getClass(), o);
		Computation result = matcherCode.visitReferenceType(value);

		result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("recursive(Object.class)");
	}

	@Test
	void testVisitReferenceTypeCheckSkipped() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));
		context.addHint(value, skipChecks());

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result).isNull();
	}

	@Test
	void testVisitReferenceTypeRevisited() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));
		matcherCode.visitReferenceType(value);

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("recursive(Dubble.class)");
	}

	@Test
	void testVisitImmutableType() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));

		Computation result = matcherCode.visitImmutableType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("equalTo(new BigInteger(\"42\"))");
	}

	@Test
	void testVisitImmutableTypeCheckSkipped() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));
		context.addHint(value, skipChecks());

		Computation result = matcherCode.visitImmutableType(value);

		assertThat(result).isNull();
	}

	@Test
	void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("equalTo(42)");
	}

	@Test
	void testVisitValueTypeCheckSkipped() throws Exception {
		SerializedLiteral value = literal(int.class, 42);
		context.addHint(value, skipChecks());

		Computation result = matcherCode.visitValueType(value);

		assertThat(result).isNull();
	}

	@Test
	void testTemporaryLocal() throws Exception {
		assertThat(context.temporaryLocal()).isEqualTo("temp1");
		assertThat(context.temporaryLocal()).isEqualTo("temp2");
	}

	@Test
	void testNewLocal() throws Exception {
		assertThat(context.newLocal("var")).isEqualTo("var1");
		assertThat(context.newLocal("var")).isEqualTo("var2");
	}

	private SkipChecks skipChecks() {
		return new SkipChecks() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return SkipChecks.class;
			}
		};
	}

}
