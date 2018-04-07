package net.amygdalum.testrecorder.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;

public class DefaultDeserializerContextTest {

	@Test
	public void testGetHints() throws Exception {
		DefaultDeserializerContext context = new DefaultDeserializerContext();
		assertThat(context.newWithHints(new String[] { "1" }).getHints(Integer.class).toArray(Integer[]::new)).isEmpty();
		assertThat(context.newWithHints(new Integer[] { 1 }).getHints(Integer.class).toArray(Integer[]::new)).containsExactly(1);
	}

	@Test
	public void testGetHint() throws Exception {
		DefaultDeserializerContext context = new DefaultDeserializerContext();
		assertThat(context.newWithHints(new String[] { "1" }).getHint(Integer.class).isPresent()).isFalse();
		assertThat(context.newWithHints(new Integer[] { 1 }).getHint(Integer.class).get()).isEqualTo(1);
	}

}
