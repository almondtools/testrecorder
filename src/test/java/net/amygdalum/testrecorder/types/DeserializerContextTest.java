package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.values.SerializedObject;

public class DeserializerContextTest {

	@Test
	void testNULL() throws Exception {
		assertThat(DeserializerContext.NULL.getParent()).isNull();
		assertThat(DeserializerContext.NULL.newWithHints(new Object[0])).isSameAs(DeserializerContext.NULL);
		assertThat(DeserializerContext.NULL.getHint(Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.getHints(Object.class)).isEmpty();
		assertThat(DeserializerContext.NULL.refCount(literal(1))).isEqualTo(0);
		assertThatCode(() -> DeserializerContext.NULL.ref(new SerializedObject(Object.class), literal(1))).doesNotThrowAnyException();
		assertThatCode(() -> DeserializerContext.NULL.staticRef(literal(1))).doesNotThrowAnyException();
		assertThat(DeserializerContext.NULL.closureOf(new SerializedObject(Object.class))).isEmpty();
		assertThat(DeserializerContext.NULL.getTypes()).isNull();
		assertThat(DeserializerContext.NULL.adapt("exp", Object.class, Object.class)).isNull();
		assertThat(DeserializerContext.NULL.defines(literal(1))).isFalse();
		assertThat(DeserializerContext.NULL.getDefinition(literal(1))).isNull();
		assertThat(DeserializerContext.NULL.needsAdaptation(Object.class, Object.class)).isFalse();
		assertThat(DeserializerContext.NULL.forVariable(literal(1), null)).isNull();
		assertThat(DeserializerContext.NULL.temporaryLocal()).isNull();
		assertThat(DeserializerContext.NULL.newLocal("var")).isNull();
		assertThat(DeserializerContext.NULL.localVariable(literal(1))).isNull();
		assertThatCode(()->DeserializerContext.NULL.resetVariable(literal(1))).doesNotThrowAnyException();;
		assertThatCode(()->DeserializerContext.NULL.finishVariable(literal(1))).doesNotThrowAnyException();;
		assertThat(DeserializerContext.NULL.getLocals()).isNull();
		assertThat(DeserializerContext.NULL.isComputed(literal(1))).isFalse();
		assertThat(DeserializerContext.NULL.resolve(1)).isEmpty();
	}
}
