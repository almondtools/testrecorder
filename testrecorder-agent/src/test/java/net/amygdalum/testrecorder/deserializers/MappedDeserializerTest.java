package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MappedDeserializerTest {

	private Function<Integer, Long> mapping;
	private Deserializer<Integer> deserializer;
	private MappedDeserializer<Long, Integer> mappedDeserializer;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void before() throws Exception {
		deserializer = mock(Deserializer.class);
		mapping = x -> (long) x;
		mappedDeserializer = new MappedDeserializer<>(deserializer, mapping);
	}

	@Test
	public void testVisitField() throws Exception {
		SerializedField field = new SerializedField(Simple.class, "str", String.class, literal("v"));
		when(deserializer.visitField(field, NULL)).thenReturn(2);

		assertThat(mappedDeserializer.visitField(field, NULL)).isEqualTo(2l);
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedReferenceType object = new SerializedObject(Simple.class);
		when(deserializer.visitReferenceType(object, NULL)).thenReturn(3);

		assertThat(mappedDeserializer.visitReferenceType(object, NULL)).isEqualTo(3l);
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutableType object = new SerializedImmutable<>(BigInteger.class);
		when(deserializer.visitImmutableType(object, NULL)).thenReturn(4);

		assertThat(mappedDeserializer.visitImmutableType(object, NULL)).isEqualTo(4l);
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedValueType object = SerializedLiteral.literal("lit");
		when(deserializer.visitValueType(object, NULL)).thenReturn(5);

		assertThat(mappedDeserializer.visitValueType(object, NULL)).isEqualTo(5l);
	}

}
