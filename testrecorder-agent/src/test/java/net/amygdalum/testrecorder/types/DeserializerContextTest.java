package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.values.SerializedNull;

public class DeserializerContextTest {

	@Test
	void testNULL() throws Exception {
		SerializedValue anyValue = Mockito.mock(SerializedValue.class);
		SerializedReferenceType anyRef = Mockito.mock(SerializedReferenceType.class);
		
		assertThat(DeserializerContext.NULL.newIsolatedContext(null, null)).isSameAs(DeserializerContext.NULL);
		assertThatCode(() -> DeserializerContext.NULL.addHint(Object.class, null)).doesNotThrowAnyException();
		assertThat(DeserializerContext.NULL.getHint(nullInstance(), Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.getHint(Object.class, Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.getHints(nullInstance(), Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.getHints(Object.class, Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.refCount(anyValue)).isEqualTo(0);
		assertThatCode(() -> DeserializerContext.NULL.ref(anyRef, anyValue)).doesNotThrowAnyException();
		assertThatCode(() -> DeserializerContext.NULL.staticRef(anyValue)).doesNotThrowAnyException();
		assertThat(DeserializerContext.NULL.closureOf(anyRef)).isEmpty();
		assertThat(DeserializerContext.NULL.getTypes()).isNull();
		assertThat(DeserializerContext.NULL.adapt("exp", Object.class, Object.class)).isNull();
		assertThat(DeserializerContext.NULL.defines(anyValue)).isFalse();
		assertThat(DeserializerContext.NULL.getDefinition(anyValue)).isNull();
		assertThat(DeserializerContext.NULL.needsAdaptation(Object.class, Object.class)).isFalse();
		assertThat(DeserializerContext.NULL.forVariable(null, null, null)).isNull();
		assertThat(DeserializerContext.NULL.localVariable(null, null)).isNull();
		assertThat(DeserializerContext.NULL.temporaryLocal()).isNull();
		assertThat(DeserializerContext.NULL.newLocal("var")).isNull();
		assertThatCode(() -> DeserializerContext.NULL.resetVariable(anyValue)).doesNotThrowAnyException();
		assertThatCode(() -> DeserializerContext.NULL.finishVariable(anyValue)).doesNotThrowAnyException();
		assertThat(DeserializerContext.NULL.getLocals()).isNull();
		assertThat(DeserializerContext.NULL.isComputed(anyValue)).isFalse();
		assertThat(DeserializerContext.NULL.resolve(1)).isEmpty();
		assertThat(DeserializerContext.NULL.withRole(nullInstance(), (Function<SerializedNull, String>) s -> s.toString())).isEqualTo("null");
	}

}
