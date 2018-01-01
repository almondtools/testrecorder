package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class TripleTest {

	@Test
	public void testTriple() throws Exception {
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f).getElement1()).isEqualTo("string");
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f).getElement2()).isEqualTo(11);
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f).getElement3()).isEqualTo(0.1f);
	}

	@Test
	public void testEqualsHashCode() throws Exception {
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f)).satisfies(defaultEquality()
			.andEqualTo(new Triple<String, Integer, Float>("string", 11, 0.1f))
			.andNotEqualTo(new Triple<String, Integer, Float>("s", 11, 0.1f))
			.andNotEqualTo(new Triple<String, Integer, Float>("string", 12, 0.1f))
			.andNotEqualTo(new Triple<String, Integer, Float>("string", 11, 0.2f))
			.andNotEqualTo(new Triple<String, String, String>("string", "string", "string"))
			.andNotEqualTo(new Triple<Integer, Integer, Integer>(11, 11, 11))
			.conventions());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testZip() throws Exception {
		Triple<String, Integer, Float>[] zipped = Triple.zip(new String[] { "s1", "s2" }, new Integer[] { 1, 2 }, new Float[] { 0.1f, 0.2f });
		assertThat(zipped).containsExactly(new Triple<>("s1", 1, 0.1f), new Triple<>("s2", 2, 0.2f));
	}

	@Test
	public void testZipWithUnmatchedArraLength2() throws Exception {
		assertThatThrownBy(() -> Triple.zip(new String[] { "s1", "s2" }, new Integer[] { 1 }, new Float[] { 0.1f, 0.2f })).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testZipWithUnmatchedArraLength3() throws Exception {
		assertThatThrownBy(() -> Triple.zip(new String[] { "s1", "s2" }, new Integer[] { 1, 2 }, new Float[] { 0.1f })).isInstanceOf(IllegalArgumentException.class);
	}

}
