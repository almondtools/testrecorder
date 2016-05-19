package net.amygdalum.testrecorder.deserializers.builder;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.containsString;
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
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.profile.DefaultSerializationProfile;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ObjectToSetupCodeTest {

	private ConfigurableSerializerFacade facade;
	private ObjectToSetupCode setupCode;
	
	@Before
	public void before() throws Exception {
		facade = new ConfigurableSerializerFacade(new DefaultSerializationProfile());
		setupCode = new ObjectToSetupCode(getClass());
	}
	
	@Test
	public void testVisitField() throws Exception {
		DefaultListSerializer serializer = new DefaultListSerializer(facade);
		Type type = parameterized(ArrayList.class,null, String.class);
		SerializedList value = serializer.generate(type, type);
		serializer.populate(value, visible("Foo","Bar"));
		
		Computation result = setupCode.visitField(new SerializedField(ListContainer.class, "list", type, value));
		
		assertThat(result.getStatements().toString(), containsPattern("ArrayList<String> list1 = new ArrayList*"));
		assertThat(result.getValue(), equalTo("ArrayList<String> list = list1;"));
	}

	@Test
	public void testVisitFieldWithHiddenTypeAndVisibleResult() throws Exception {
		SerializedObject value = object(parameterized(innerType(ObjectToSetupCodeTest.class, "HiddenList"),null, String.class), hidden("Foo","Bar"));
		
		Computation result = setupCode.visitField(new SerializedField(ListContainer.class, "list", parameterized(List.class,null, String.class), value));
		
		assertThat(result.getStatements().toString(), containsPattern("Wrapped hiddenList2 = new GenericObject*"));
		assertThat(result.getValue(), equalTo("List<String> list = (List<String>) hiddenList2.value();"));
	}

	@Test
	public void testVisitFieldWithHiddenTypeAndHiddenResult() throws Exception {
		SerializedObject value = object(parameterized(innerType(ObjectToSetupCodeTest.class, "HiddenList"),null, String.class), hidden("Foo","Bar"));
		
		Computation result = setupCode.visitField(new SerializedField(ListContainer.class, "list", parameterized(innerType(ObjectToSetupCodeTest.class, "HiddenList"),null, String.class), value));
		
		assertThat(result.getStatements().toString(), containsPattern("Wrapped hiddenList2 = new GenericObject*"));
		assertThat(result.getValue(), equalTo("Wrapped list = hiddenList2;"));
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedObject value = object(Name.class, new Name("Foo","Bar"));
		
		Computation result = setupCode.visitReferenceType(value);
		
		assertThat(result.getStatements().toString(), containsString("Name name1 = new Name(\"Foo\", \"Bar\");"));
		assertThat(result.getValue(), equalTo("name1"));
	}

	@Test
	public void testVisitReferenceTypeRevisited() throws Exception {
		SerializedObject value = object(Name.class, new Name("Foo","Bar"));
		setupCode.visitReferenceType(value);
		
		Computation result = setupCode.visitReferenceType(value);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("name1"));
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutable<BigInteger> value = bigInteger(BigInteger.valueOf(42));
		
		Computation result = setupCode.visitImmutableType(value);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("new BigInteger(\"42\")"));
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);
		
		Computation result = setupCode.visitValueType(value);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("42"));
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
	
	@SafeVarargs
	private static <S> HiddenList<S> hidden(S... elements) {
		HiddenList<S> hiddenList = new HiddenList<>();
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
