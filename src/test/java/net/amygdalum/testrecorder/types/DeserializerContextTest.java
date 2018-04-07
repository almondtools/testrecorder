package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DeserializerContextTest {

	@Test
	void testNULL() throws Exception {
		SerializedValue anyValue = Mockito.mock(SerializedValue.class);
		SerializedReferenceType anyRef = Mockito.mock(SerializedReferenceType.class);
		
		assertThat(DeserializerContext.NULL.getParent()).isNull();
		assertThat(DeserializerContext.NULL.newWithHints(new Object[0])).isSameAs(DeserializerContext.NULL);
		assertThat(DeserializerContext.NULL.getHint(Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.getHints(Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.refCount(anyValue)).isEqualTo(0);
		assertThatCode(() -> DeserializerContext.NULL.ref(anyRef, anyValue)).doesNotThrowAnyException();
		assertThatCode(() -> DeserializerContext.NULL.staticRef(anyValue)).doesNotThrowAnyException();
		assertThat(DeserializerContext.NULL.closureOf(anyRef)).isEmpty();
		assertThat(DeserializerContext.NULL.getTypes()).isNull();
		assertThat(DeserializerContext.NULL.adapt("exp", Object.class, Object.class)).isNull();
		assertThat(DeserializerContext.NULL.defines(anyValue)).isFalse();
		assertThat(DeserializerContext.NULL.getDefinition(anyValue)).isNull();
		assertThat(DeserializerContext.NULL.needsAdaptation(Object.class, Object.class)).isFalse();
		assertThat(DeserializerContext.NULL.forVariable(anyValue, null)).isNull();
		assertThat(DeserializerContext.NULL.temporaryLocal()).isNull();
		assertThat(DeserializerContext.NULL.newLocal("var")).isNull();
		assertThat(DeserializerContext.NULL.localVariable(anyValue)).isNull();
		assertThatCode(() -> DeserializerContext.NULL.resetVariable(anyValue)).doesNotThrowAnyException();
		assertThatCode(() -> DeserializerContext.NULL.finishVariable(anyValue)).doesNotThrowAnyException();
		assertThat(DeserializerContext.NULL.getLocals()).isNull();
		assertThat(DeserializerContext.NULL.isComputed(anyValue)).isFalse();
		assertThat(DeserializerContext.NULL.resolve(1)).isEmpty();
	}

}
