package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.types.OverrideSerializer;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.ASerializedValue;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ConfigurableSerializerFacadeTest {

	@Test
	public void testLoadSerializersFromConfig() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig()
			.loading(Serializer.class, args -> new TestSerializer(TestClass.class, new ASerializedValue(TestClass.class, 1)))
			.loading(Serializer.class, args -> new TestSerializer(OtherClass.class, new ASerializedValue(OtherClass.class, 2))));

		SerializerSession session = facade.newSession();

		assertThat(facade.serialize(TestClass.class, new TestClass(), session))
			.isInstanceOf(ASerializedValue.class)
			.returns(TestClass.class, SerializedValue::getType)
			.returns(1, object -> ((ASerializedValue) object).getId());
		assertThat(facade.serialize(OtherClass.class, new OtherClass(), session))
			.isInstanceOf(ASerializedValue.class)
			.returns(OtherClass.class, SerializedValue::getType)
			.returns(2, object -> ((ASerializedValue) object).getId());
	}

	@Test
	public void testLoadCollidingSerializersFromConfigPriorityOnFirst() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig()
			.loading(Serializer.class, args -> new TestSerializer(TestClass.class, new ASerializedValue(TestClass.class, 1)))
			.loading(Serializer.class, args -> new TestSerializer(TestClass.class, new ASerializedValue(TestClass.class, 2))));

		SerializerSession session = facade.newSession();

		assertThat(facade.serialize(TestClass.class, new TestClass(), session))
			.isInstanceOf(ASerializedValue.class)
			.returns(TestClass.class, SerializedValue::getType)
			.returns(1, object -> ((ASerializedValue) object).getId());
	}

	@Test
	public void testLoadCollidingSerializersFromConfigPriorityOnReplacing() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig()
			.loading(Serializer.class, args -> new TestSerializer(TestClass.class, new ASerializedValue(TestClass.class, 1)))
			.loading(Serializer.class, args -> new OverridingSerializer(TestClass.class, new ASerializedValue(TestClass.class, 2))));

		SerializerSession session = facade.newSession();

		assertThat(facade.serialize(TestClass.class, new TestClass(), session))
			.isInstanceOf(ASerializedValue.class)
			.returns(TestClass.class, SerializedValue::getType)
			.returns(2, object -> ((ASerializedValue) object).getId());
	}

	@Test
	public void testLoadCollidingSerializersFromConfigPriorityOnReplacingReverseOrder() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig()
			.loading(Serializer.class, args -> new OverridingSerializer(TestClass.class, new ASerializedValue(TestClass.class, 2)))
			.loading(Serializer.class, args -> new TestSerializer(TestClass.class, new ASerializedValue(TestClass.class, 1))));
		
		SerializerSession session = facade.newSession();
		
		assertThat(facade.serialize(TestClass.class, new TestClass(), session))
		.isInstanceOf(ASerializedValue.class)
		.returns(TestClass.class, SerializedValue::getType)
		.returns(2, object -> ((ASerializedValue) object).getId());
	}
	
	@Test
	public void testSerializeOnNull() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig());

		assertThat(facade.serialize(String.class, null, facade.newSession())).isEqualTo(SerializedNull.nullInstance(String.class));
	}

	@Test
	public void testSerializeOnLiteral() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig());

		assertThat(facade.serialize(String.class, "strliteral", facade.newSession())).isEqualTo(SerializedLiteral.literal("strliteral"));
		assertThat(facade.serialize(int.class, 22, facade.newSession())).isEqualTo(SerializedLiteral.literal(int.class, 22));
		assertThat(facade.serialize(Integer.class, 22, facade.newSession())).isEqualTo(SerializedLiteral.literal(Integer.class, 22));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSerializeOnOther() throws Exception {
		Serializer<SerializedObject> serializer = Mockito.mock(Serializer.class);
		when(serializer.getMatchingClasses()).thenReturn(asList(TestClass.class));
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig()
			.loading(Serializer.class, args -> serializer));

		SerializerSession session = facade.newSession();
		SerializedObject expectedResult = new SerializedObject(TestClass.class);

		when(serializer.generate(TestClass.class, session)).thenReturn(expectedResult);

		TestClass value = new TestClass();
		SerializedValue result = facade.serialize(TestClass.class, value, session);

		assertThat(result).isSameAs(expectedResult);
		verify(serializer).populate(expectedResult, value, session);
	}

	@Test
	public void testSerializeArrayOnEmpty() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig());

		SerializedValue[] serialize = facade.serialize(new Type[0], new Object[0], facade.newSession());

		assertThat(serialize).isEmpty();
	}

	@Test
	public void testSerializeArray() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig());

		SerializedValue[] serialize = facade.serialize(new Type[] { String.class }, new Object[] { "str" }, facade.newSession());

		assertThat(serialize).containsExactly(SerializedLiteral.literal(String.class, "str"));
	}

	@Test
	public void testSerializeFieldObject() throws Exception {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(defaultConfig());

		SerializedField serialized = facade.serialize(getDeclaredField(TestClass.class, "testField"), new TestClass(), facade.newSession());

		assertThat(serialized.getName()).isEqualTo("testField");
		assertThat(serialized.getDeclaringClass()).isEqualTo(TestClass.class);
		assertThat(serialized.getType()).isEqualTo(int.class);
		assertThat(serialized.getValue()).isEqualTo(literal(int.class, 42));
	}

	public static class OtherClass {

	}

	public static class TestClass {

		private int testField;
		@TypeHint
		@ComplexHint(text = "str", value = 1)
		private String hintedField;

		public TestClass() {
			testField = 42;
			hintedField = "withHint";
		}

		public @ResultHint(value = int.class) int TestMethod(@ArgHint(value = int.class) int factor) {
			return testField * factor;
		}
	}

	private static class TestSerializer implements Serializer<SerializedValue> {

		private Class<?> clazz;
		private SerializedValue value;

		public TestSerializer(Class<?> clazz, SerializedValue value) {
			this.clazz = clazz;
			this.value = value;
		}

		@Override
		public List<Class<?>> getMatchingClasses() {
			return asList(clazz);
		}

		@Override
		public SerializedValue generate(Class<?> type, SerializerSession session) {
			return value;
		}

		@Override
		public void populate(SerializedValue serializedObject, Object object, SerializerSession session) {
		}

	}

	@OverrideSerializer(GenericSerializer.class)
	@OverrideSerializer(TestSerializer.class)
	private static class OverridingSerializer extends TestSerializer {

		public OverridingSerializer(Class<?> clazz, SerializedValue value) {
			super(clazz, value);
		}

	}

}
