package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.xrayinterface.XRayInterface;

public class ConfigurableSerializerFacadeTest {

    private ConfigurableSerializerFacade facade;
    private OpenFacade openFacade;

    @BeforeEach
    public void before() throws Exception {
        facade = new ConfigurableSerializerFacade(defaultConfig());
        openFacade = XRayInterface.xray(facade).to(OpenFacade.class);
    }

    @Test
	    public void testLogTypeObjectOnNull() throws Exception {
	        assertThat(facade.serialize(String.class, null)).isEqualTo(SerializedNull.nullInstance(String.class));
	    }

    @Test
	    public void testLogTypeObjectOnLiteral() throws Exception {
	        assertThat(facade.serialize(String.class, "strliteral")).isEqualTo(SerializedLiteral.literal("strliteral"));
	        assertThat(facade.serialize(int.class, 22)).isEqualTo(SerializedLiteral.literal(int.class, 22));
	        assertThat(facade.serialize(Integer.class, 22)).isEqualTo(SerializedLiteral.literal(Integer.class, 22));
	    }

    @SuppressWarnings("unchecked")
	    @Test
	    public void testLogTypeObjectOnOther() throws Exception {
	        Serializer<SerializedObject> serializer = Mockito.mock(Serializer.class);
	        openFacade.getSerializers().put(TestClass.class, serializer);
	        SerializedObject expectedResult = new SerializedObject(TestClass.class);
	
	        when(serializer.generate(TestClass.class)).thenReturn(expectedResult);
	
	        TestClass value = new TestClass();
	        SerializedValue result = facade.serialize(TestClass.class, value);
	
	        assertThat(result).isSameAs(expectedResult);
	        verify(serializer).populate(expectedResult, value);
	    }

    @Test
	    public void testLogTypeArrayObjectArrayOnEmpty() throws Exception {
	        SerializedValue[] serialize = facade.serialize(new Type[0], new Object[0]);
	
	        assertThat(serialize).isEmpty();
	    }

    @Test
	    public void testLogTypeArrayObjectArray() throws Exception {
	        SerializedValue[] serialize = facade.serialize(new Type[] { String.class }, new Object[] { "str" });
	
	        assertThat(serialize).containsExactly(SerializedLiteral.literal(String.class, "str"));
	    }

    @Test
	    public void testLogFieldObject() throws Exception {
	        SerializedField serialized = facade.serialize(getDeclaredField(TestClass.class, "testField"), new TestClass());
	
	        assertThat(serialized.getName()).isEqualTo("testField");
	        assertThat(serialized.getDeclaringClass()).isEqualTo(TestClass.class);
	        assertThat(serialized.getType()).isEqualTo(int.class);
	        assertThat(serialized.getValue()).isEqualTo(literal(int.class, 42));
	    }

    interface OpenFacade {
        Map<Class<?>, Serializer<?>> getSerializers();
    }

    public class TestClass {

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

}
