package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultSequenceAdaptorTest {

	private AgentConfiguration config;
	private DefaultSequenceAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultSequenceAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeExplicitelyTypedList() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, BigInteger.class));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsInOrder(BigInteger.class, "
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeList() throws Exception {
		SerializedList value = new SerializedList(List.class);
		value.useAs(parameterized(List.class, null, BigInteger.class));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsInOrder(BigInteger.class, "
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeRawList() throws Exception {
		SerializedList value = new SerializedList(List.class);
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsInOrder("
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeEmptyList() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, BigInteger.class));

		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("empty(BigInteger.class)");
	}

	public void testTryDeserializeEmptyRawList() throws Exception {
		SerializedList value = new SerializedList(List.class);

		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("empty()");
	}

	@Test
	public void testTryDeserializeGenericComponents() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, parameterized(List.class, null, String.class)));
		value.add(list(parameterized(List.class, null, String.class), literal("str1")));
		value.add(list(parameterized(List.class, null, String.class), literal("str2"), literal("str3")));
		value.add(list(parameterized(List.class, null, String.class)));

		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsInOrder((Class<List<String>>) (Class) List.class, "
			+ "containsInOrder(String.class, \"str1\"), "
			+ "containsInOrder(String.class, \"str2\", \"str3\"), "
			+ "empty(String.class))");
	}

	@Test
	public void testTryDeserializeHiddenComponents() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, Hidden.classOfCompletelyHidden()));
		value.add(new SerializedObject(Hidden.classOfCompletelyHidden()));

		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern(""
			+ "containsInOrder(Object.class, "
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")))");
	}

	private SerializedList list(Type type, SerializedValue... values) {
		SerializedList list = new SerializedList(baseType(type));
		list.useAs(type);
		for (SerializedValue value : values) {
			list.add(value);
		}
		return list;
	}

	private Deserializer generator() {
		return new MatcherGenerators(new Adaptors().load(config.loadConfigurations(MatcherGenerator.class))).newGenerator(context);
	}

}
