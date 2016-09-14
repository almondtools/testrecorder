package net.amygdalum.testrecorder.deserializers.matcher;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MatcherGeneratorsTest {

	private ConfigurableSerializerFacade facade;
	private MatcherGenerators matcherCode;

	@Before
	public void before() throws Exception {
		facade = new ConfigurableSerializerFacade(new DefaultTestRecorderAgentConfig());
		matcherCode = new MatcherGenerators(getClass());
	}

	@Test
	public void testNullIsSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(nullInstance(Object.class)), is(true));
		assertThat(matcherCode.simpleValue(nullInstance(Object.class)).getStatements(), empty());
		assertThat(matcherCode.simpleValue(nullInstance(Object.class)).getValue(), equalTo("null"));
	}

	@Test
	public void testLiteralIsSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(literal("str")), is(true));
		assertThat(matcherCode.simpleValue(literal("str")).getStatements(), empty());
		assertThat(matcherCode.simpleValue(literal("str")).getValue(), equalTo("\"str\""));
	}

	@Test
	public void testOtherIsNotSimpleValue() throws Exception {
		assertThat(matcherCode.isSimpleValue(object(Name.class, new Name("Foo", "Bar"))), is(false));
		assertThat(matcherCode.simpleValue(object(Name.class, new Name("Foo", "Bar"))).getStatements(), empty());
		assertThat(matcherCode.simpleValue(object(Name.class, new Name("Foo", "Bar"))).getValue(), containsPattern("new GenericMatcher() {*"
			+ "firstName = \"Foo\"*"
			+ "lastName = \"Bar\"*"
			+ "}.matching(Name.class)"));
	}

	@Test
	public void testVisitField() throws Exception {
		DefaultListSerializer serializer = new DefaultListSerializer(facade);
		Type type = parameterized(ArrayList.class, null, String.class);
		SerializedList value = serializer.generate(type, type);
		serializer.populate(value, visible("Foo", "Bar"));

		Computation result = matcherCode.visitField(new SerializedField(ListContainer.class, "list", type, value));

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("Matcher<?> list = containsInOrder(String.class, \"Foo\", \"Bar\");"));
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedObject value = object(Name.class, new Name("Foo", "Bar"));

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), containsPattern("new GenericMatcher() {*"
			+ "firstName = \"Foo\"*"
			+ "lastName = \"Bar\"*"
			+ "}.matching(Name.class)"));
	}

	@Test
	public void testVisitReferenceTypeRevisited() throws Exception {
		SerializedObject value = object(Name.class, new Name("Foo", "Bar"));
		matcherCode.visitReferenceType(value);

		Computation result = matcherCode.visitReferenceType(value);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("recursive(Name.class)"));
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutable<BigInteger> value = bigInteger(BigInteger.valueOf(42));

		Computation result = matcherCode.visitImmutableType(value);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("equalTo(new BigInteger(\"42\"))"));
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("equalTo(42)"));
	}

	private SerializedObject object(Type type, Object object) {
		GenericSerializer serializer = new GenericSerializer(facade);
		SerializedObject value = (SerializedObject) serializer.generate(type, type);
		serializer.populate(value, object);
		return value;
	}

	private SerializedImmutable<BigInteger> bigInteger(BigInteger object) {
		BigIntegerSerializer serializer = new BigIntegerSerializer(facade);
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, BigInteger.class);
		serializer.populate(value, object);
		return value;
	}

	@SafeVarargs
	private static <S> ArrayList<S> visible(S... elements) {
		ArrayList<S> hiddenList = new ArrayList<>();
		for (S element : elements) {
			hiddenList.add(element);
		}
		return hiddenList;
	}

	@SuppressWarnings("unused")
	private static class ListContainer {
		private HiddenList<String> list;

		public ListContainer(HiddenList<String> list) {
			this.list = list;
		}

		public List<String> getList() {
			return list;
		}
	}

	private static class HiddenList<T> extends ArrayList<T> {
	}

	public static class Name {
		public String firstName;
		public String lastName;

		public Name(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

	}

}
