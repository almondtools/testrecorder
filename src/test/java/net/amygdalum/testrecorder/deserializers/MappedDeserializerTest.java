package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MappedDeserializerTest {

    private Function<Integer, Long> mapping;
    private Deserializer<Integer> deserializer;
    private MappedDeserializer<Long, Integer> mappedDeserializer;

    @SuppressWarnings("unchecked")
    @Before
    public void before() throws Exception {
        deserializer = mock(Deserializer.class);
        mapping = x -> (long) x;
        mappedDeserializer = new MappedDeserializer<>(deserializer, mapping);
    }

    @Test
    public void testVisitField() throws Exception {
        SerializedField field = new SerializedField(TestObject.class, "field", String.class, literal("v"));
        when(deserializer.visitField(field, DeserializerContext.NULL)).thenReturn(2);

        assertThat(mappedDeserializer.visitField(field), equalTo(2l));

    }

    @Test
    public void testVisitReferenceType() throws Exception {
        SerializedReferenceType object = new SerializedObject(TestObject.class);
        when(deserializer.visitReferenceType(object, DeserializerContext.NULL)).thenReturn(3);

        assertThat(mappedDeserializer.visitReferenceType(object), equalTo(3l));
    }

    @Test
    public void testVisitImmutableType() throws Exception {
        SerializedImmutableType object = new SerializedImmutable<>(BigInteger.class);
        when(deserializer.visitImmutableType(object, DeserializerContext.NULL)).thenReturn(4);

        assertThat(mappedDeserializer.visitImmutableType(object), equalTo(4l));
    }

    @Test
    public void testVisitValueType() throws Exception {
        SerializedValueType object = SerializedLiteral.literal("lit");
        when(deserializer.visitValueType(object, DeserializerContext.NULL)).thenReturn(5);

        assertThat(mappedDeserializer.visitValueType(object), equalTo(5l));
    }

    @SuppressWarnings("unused")
    public static class TestObject {

        private String field;
    }

}
