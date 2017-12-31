package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class DefaultDeserializerContextTest {

	@Test
	public void testGetHints() throws Exception {
		assertThat(NULL.newWithHints(new String[] { "1" }).getHints(Integer.class).toArray(Integer[]::new), emptyArray());
		assertThat(NULL.newWithHints(new Integer[] { 1 }).getHints(Integer.class).toArray(Integer[]::new)).containsExactly(1);
	}

	@Test
	public void testGetHint() throws Exception {
		assertThat(NULL.newWithHints(new String[] { "1" }).getHint(Integer.class).isPresent(), is(false));
		assertThat(NULL.newWithHints(new Integer[] { 1 }).getHint(Integer.class).get()).isEqualTo(1);
	}

}
