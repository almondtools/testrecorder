package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class AbstractCompositeSerializerTest {

	private AbstractCompositeSerializer serializer;
	private SerializerSession session;

	@BeforeEach
	public void before() throws Exception {
		serializer = new AbstractCompositeSerializer() {
		};
		session = Mockito.mock(SerializerSession.class);
	}

	@Test
	void testResolvedFieldOfSerializerSessionObjectField() throws Exception {
		Fields object = new Fields("fieldvalue");
		Field field = Fields.class.getDeclaredField("field");

		SerializedField serializedField = serializer.resolvedFieldOf(session, object, field);

		assertThat(serializedField.getDeclaringClass()).isEqualTo(Fields.class);
		assertThat(serializedField.getName()).isEqualTo("field");
		assertThat(serializedField.getType()).isEqualTo(Object.class);
		assertThat(serializedField.getValue()).isEqualTo(literal("fieldvalue"));
	}

	@Test
	void testResolvedFieldOfSerializerSessionObjectFieldNotExisting() throws Exception {
		Fields object = new Fields("fieldvalue");
		Field field = Other.class.getDeclaredField("field");

		assertThatThrownBy(() -> serializer.resolvedFieldOf(session, object, field)).isInstanceOf(SerializationException.class);
	}

	@Test
	void testResolvedFieldOfSerializerSessionObjectClassString() throws Exception {
		Fields object = new Fields("fieldvalue");

		SerializedField serializedField = serializer.resolvedFieldOf(session, object, Fields.class, "field");

		assertThat(serializedField.getDeclaringClass()).isEqualTo(Fields.class);
		assertThat(serializedField.getName()).isEqualTo("field");
		assertThat(serializedField.getType()).isEqualTo(Object.class);
		assertThat(serializedField.getValue()).isEqualTo(literal("fieldvalue"));
	}

	@Test
	void testResolvedFieldOfSerializerSessionObjectClassStringNotExisting() throws Exception {
		Fields object = new Fields("fieldvalue");

		assertThatThrownBy(() -> serializer.resolvedFieldOf(session, object, Fields.class, "nofield")).isInstanceOf(SerializationException.class);
	}

	@Test
	void testResolvedValueOfPrimitives() throws Exception {
		assertThat(serializer.resolvedValueOf(session, int.class, 1)).isEqualTo(literal(int.class, 1));
		assertThat(serializer.resolvedValueOf(session, Integer.class, 1)).isEqualTo(literal(1));
		assertThat(serializer.resolvedValueOf(session, String.class, "str")).isEqualTo(literal("str"));
	}

	@Test
	void testResolvedValueOfNull() throws Exception {
		assertThat(serializer.resolvedValueOf(session, null, null)).isEqualTo(nullInstance());
		assertThat(serializer.resolvedValueOf(session, Integer.class, null)).isEqualTo(nullInstanceFor(Integer.class));
		assertThat(serializer.resolvedValueOf(session, ((Callable<String>) () -> "result").getClass(), null)).isEqualTo(nullInstance());
	}

	private SerializedNull nullInstanceFor(Class<?> type) {
		SerializedNull nullInstance = nullInstance();
		nullInstance.useAs(type);
		return nullInstance;
	}

	@Test
	void testResolvedValueOfResolved() throws Exception {
		Fields value = new Fields("fieldvalue");

		assertThatThrownBy(() -> serializer.resolvedValueOf(session, Fields.class, value)).isInstanceOf(SerializationException.class);
	}

	@Test
	void testResolvedValueOfUnresolved() throws Exception {
		Fields value = new Fields("fieldvalue");
		SerializedObject serializedObject = new SerializedObject(Fields.class);
		Mockito.when(session.ref(value, Fields.class)).thenReturn(serializedObject);

		assertThat(serializer.resolvedValueOf(session, Fields.class, value)).isSameAs(serializedObject);
	}

	@Test
	void testFieldOfObjectField() throws Exception {
		Fields object = new Fields("field");
		Field field = Fields.class.getDeclaredField("field");

		Object value = serializer.fieldOf(object, field);

		assertThat(value).isEqualTo("field");
	}

	@Test
	void testFieldOfObjectFieldNotExisting() throws Exception {
		Fields object = new Fields("field");
		Field field = Other.class.getDeclaredField("field");

		assertThatThrownBy(() -> serializer.fieldOf(object, field)).isInstanceOf(SerializationException.class);
	}

	@Test
	void testFieldOfObjectClassString() throws Exception {
		Fields object = new Fields("field");

		Object value = serializer.fieldOf(object, Fields.class, "field");

		assertThat(value).isEqualTo("field");
	}

	@Test
	void testFieldOfObjectClassStringNotExisting() throws Exception {
		Fields object = new Fields("field");

		assertThatThrownBy(() -> serializer.fieldOf(object, Fields.class, "nofield")).isInstanceOf(SerializationException.class);
	}

	@SuppressWarnings("unused")
	private static class Fields {
		private Object field;

		Fields(Object field) {
			this.field = field;
		}
	}

	@SuppressWarnings("unused")
	private static class Other {
		private Object field;

		Other(Object field) {
			this.field = field;
		}
	}

}
