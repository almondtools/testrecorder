package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MappedRoleVisitorTest {

	private RoleVisitor<Integer> deserializer;
	private MappedRoleVisitor<Long, Integer> mappedDeserializer;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void before() throws Exception {
		deserializer = mock(RoleVisitor.class);
		Function<Integer, Long> mapping = x -> (long) x;
		mappedDeserializer = new MappedRoleVisitor<>(deserializer, mapping);
	}

	@Test
	public void testVisitField() throws Exception {
		SerializedField field = new SerializedField(new FieldSignature(Simple.class, String.class, "str"), literal("v"));
		when(deserializer.visitField(field)).thenReturn(2);

		assertThat(mappedDeserializer.visitField(field)).isEqualTo(2);
	}

	@Test
	public void testVisitArgument() throws Exception {
		SerializedArgument argument = new SerializedArgument(1, new MethodSignature(MyObject.class, String.class, "method", new Type[] { int.class }), literal("v"));
		when(deserializer.visitArgument(argument)).thenReturn(3);

		assertThat(mappedDeserializer.visitArgument(argument)).isEqualTo(3);
	}

	@Test
	public void testVisitResult() throws Exception {
		SerializedResult result = new SerializedResult(new MethodSignature(MyObject.class, String.class, "method", new Type[] { int.class }), literal("v"));
		when(deserializer.visitResult(result)).thenReturn(4);

		assertThat(mappedDeserializer.visitResult(result)).isEqualTo(4);
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedReferenceType object = new SerializedObject(Simple.class);
		when(deserializer.visitReferenceType(object)).thenReturn(6);

		assertThat(mappedDeserializer.visitReferenceType(object)).isEqualTo(6);
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutableType object = new SerializedImmutable<>(BigInteger.class);
		when(deserializer.visitImmutableType(object)).thenReturn(7);

		assertThat(mappedDeserializer.visitImmutableType(object)).isEqualTo(7);
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedValueType object = literal("lit");
		when(deserializer.visitValueType(object)).thenReturn(8);

		assertThat(mappedDeserializer.visitValueType(object)).isEqualTo(8);
	}

	public static class MyObject {
		String method(int arg) {
			return String.valueOf(arg);
		}
	}

}
