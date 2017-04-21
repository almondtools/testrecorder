package net.amygdalum.testrecorder.deserializers.matcher;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.deserializers.DeserializerContext.newContext;
import static net.amygdalum.testrecorder.util.Types.parameterized;
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
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
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
    public void testVisitReferenceTypePartiallyHidden() throws Exception {
        VisibleInterface o = createPartiallyHidden();
        SerializedObject value = object(o.getClass(), Hidden.VisibleInterface.class, o);

        Computation result = matcherCode.visitReferenceType(value);

        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), containsPattern("new GenericMatcher() {*"
            + "}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$PartiallyHidden\"), VisibleInterface.class)"));
    }

    @Test
    public void testVisitReferenceTypeCompletelyHidden() throws Exception {
        Object o = createCompletelyHidden();
        SerializedObject value = object(o.getClass(), o.getClass(), o);
        
        Computation result = matcherCode.visitReferenceType(value);
        
        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), containsPattern("new GenericMatcher() {*}"
            + ".matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\"))"));
    }
    
    @Test
    public void testVisitReferenceTypeComputedPartiallyHidden() throws Exception {
        VisibleInterface o = createPartiallyHidden();
        SerializedObject value = object(o.getClass(), Hidden.VisibleInterface.class, o);
        Computation result = matcherCode.visitReferenceType(value);

        result = matcherCode.visitReferenceType(value);

        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), containsPattern("recursive(VisibleInterface.class)"));
    }

    @Test
    public void testVisitReferenceTypeComputedCompletelyHidden() throws Exception {
        Object o = createCompletelyHidden();
        SerializedObject value = object(o.getClass(), o.getClass(), o);
        Computation result = matcherCode.visitReferenceType(value);

        result = matcherCode.visitReferenceType(value);
        
        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), containsPattern("recursive(Object.class)"));
    }
    
    @Test
    public void testVisitReferenceTypeCheckSkipped() throws Exception {
        SerializedObject value = object(Name.class, new Name("Foo", "Bar"));

        Computation result = matcherCode.visitReferenceType(value, newContext(skipChecks()));

        assertThat(result, nullValue());
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
    public void testVisitImmutableTypeCheckSkipped() throws Exception {
        SerializedImmutable<BigInteger> value = bigInteger(BigInteger.valueOf(42));

        Computation result = matcherCode.visitImmutableType(value, newContext(skipChecks()));

        assertThat(result, nullValue());
    }

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = matcherCode.visitValueType(value);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("equalTo(42)"));
	}

	@Test
	public void testVisitValueTypeCheckSkipped() throws Exception {
	    SerializedLiteral value = literal(int.class, 42);
	    
	    Computation result = matcherCode.visitValueType(value, newContext(skipChecks()));
	    
	    assertThat(result, nullValue());
    }
    
    @Test
    public void testSimpleMatcherSerializedValueNull() throws Exception {
        Computation result = matcherCode.simpleMatcher(SerializedNull.nullInstance(String.class));

        assertThat(result.getStatements(), empty());
        assertThat("generic matchers can match nulls and do not need matchers here", result.getValue(), equalTo("null"));
    }

    @Test
    public void testSimpleMatcherSerializedValueLiteral() throws Exception {
        Computation result = matcherCode.simpleMatcher(SerializedLiteral.literal("string"));
        
        assertThat(result.getStatements(), empty());
        assertThat("generic matchers can match literals and do not need matchers here", result.getValue(), equalTo("\"string\""));
    }
    
    @Test
    public void testSimpleMatcherSerializedValueObject() throws Exception {
        Computation result = matcherCode.simpleMatcher(object(Simple.class, new Simple()));
        
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

    private SerializedObject object(Type type, Type resultType, Object object) {
        SerializedObject o = object(type, object);
        o.setResultType(resultType);
        return o;
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

    private SkipChecks skipChecks() {
        return new SkipChecks() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return SkipChecks.class;
            }
        };
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
