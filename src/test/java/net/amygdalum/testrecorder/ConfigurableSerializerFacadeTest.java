package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.util.Types.getDeclaredField;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.SerializationProfile.Hint;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.xrayinterface.XRayInterface;

public class ConfigurableSerializerFacadeTest {

    private ConfigurableSerializerFacade facade;
    private OpenFacade openFacade;

    @Before
    public void before() throws Exception {
        facade = new ConfigurableSerializerFacade(new DefaultTestRecorderAgentConfig());
        openFacade = XRayInterface.xray(facade).to(OpenFacade.class);
    }

    @Test
    public void testSerializeTypeObjectOnNull() throws Exception {
        assertThat(facade.serialize(String.class, null), equalTo(SerializedNull.nullInstance(String.class)));
    }

    @Test
    public void testSerializeTypeObjectOnLiteral() throws Exception {
        assertThat(facade.serialize(String.class, "strliteral"), equalTo(SerializedLiteral.literal("strliteral")));
        assertThat(facade.serialize(int.class, 22), equalTo(SerializedLiteral.literal(int.class, 22)));
        assertThat(facade.serialize(Integer.class, 22), equalTo(SerializedLiteral.literal(Integer.class, 22)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializeTypeObjectOnOther() throws Exception {
        Serializer<SerializedObject> serializer = Mockito.mock(Serializer.class);
        openFacade.getSerializers().put(TestClass.class, serializer);
        SerializedObject expectedResult = new SerializedObject(TestClass.class);

        when(serializer.generate(TestClass.class, TestClass.class)).thenReturn(expectedResult);

        TestClass value = new TestClass();
        SerializedValue result = facade.serialize(TestClass.class, value);

        assertThat(result, sameInstance(expectedResult));
        verify(serializer).populate(expectedResult, value);
    }

    @Test
    public void testSerializeTypeArrayObjectArrayOnEmpty() throws Exception {
        SerializedValue[] serialize = facade.serialize(new Type[0], new Object[0]);

        assertThat(serialize, emptyArray());
    }

    @Test
    public void testSerializeTypeArrayObjectArray() throws Exception {
        SerializedValue[] serialize = facade.serialize(new Type[] { String.class }, new Object[] { "str" });

        assertThat(serialize, arrayContaining(SerializedLiteral.literal(String.class, "str")));
    }

    @Test
    public void testSerializeFieldObject() throws Exception {
        SerializedField serialized = facade.serialize(getDeclaredField(TestClass.class, "testField"), new TestClass());

        assertThat(serialized.getName(), equalTo("testField"));
        assertThat(serialized.getDeclaringClass(), equalTo(TestClass.class));
        assertThat(serialized.getType(), equalTo(int.class));
        assertThat(serialized.getValue(), equalTo(SerializedLiteral.literal(int.class, 42)));
        assertThat(serialized.getHints(), empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializeFieldObjectWithHints() throws Exception {
        TestClass obj = new TestClass();
        SerializedField serialized = facade.serialize(getDeclaredField(TestClass.class, "hintedField"), obj);

        assertThat(serialized.getName(), equalTo("hintedField"));
        assertThat(serialized.getDeclaringClass(), equalTo(TestClass.class));
        assertThat(serialized.getType(), equalTo(String.class));
        assertThat(serialized.getValue(), equalTo(SerializedLiteral.literal(String.class, "withHint")));
        List<DeserializationHint> hints = serialized.getHints();
        assertThat(hints, contains(
            instanceOf(TestHint.class),
            equalTo(new TestContextHint(TestClass.class.getDeclaredField("hintedField"), obj))
            ));
    }

    interface OpenFacade {
        Map<Class<?>, Serializer<?>> getSerializers();
    }

    @SuppressWarnings("unused")
    public class TestClass {

        private int testField;
        @Hint(TestHint.class)
        @Hint(TestContextHint.class)
        private String hintedField;

        public TestClass() {
            testField = 42;
            hintedField = "withHint";
        }
    }
    
    public static class TestHint implements DeserializationHint {
        
        @Override
        public <T extends SerializedValue, G extends Deserializer<Computation>> Computation tryDeserialize(T value, G generator, Adaptors<G> adaptors) {
            throw new DeserializationException(value.toString());
        }

    }

    public static class TestContextHint implements DeserializationHint {

        public Field field;
        public Object object;

        public TestContextHint(Field field, Object object) {
            this.field = field;
            this.object = object;
        }
        
        @Override
        public <T extends SerializedValue, G extends Deserializer<Computation>> Computation tryDeserialize(T value, G generator, Adaptors<G> adaptors) {
            throw new DeserializationException(value.toString());
        }

        @Override
        public boolean equals(Object obj) {
            TestContextHint that = (TestContextHint) obj;
            return this.field.equals(that.field)
                && this.object.equals(that.object);
        }

        @Override
        public String toString() {
            return field.getName() + ":" + object;
        }
        
    }
}
