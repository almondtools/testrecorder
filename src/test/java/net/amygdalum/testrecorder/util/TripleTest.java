package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;


public class TripleTest {

	@Test
	public void testTriple() throws Exception {
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f).getElement1(), equalTo("string"));
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f).getElement2(), equalTo(11));
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f).getElement3(), equalTo(0.1f));
	}

	@Test
	public void testEqualsHashCode() throws Exception {
		assertThat(new Triple<String, Integer, Float>("string", 11, 0.1f), satisfiesDefaultEquality()
			.andEqualTo(new Triple<String, Integer, Float>("string", 11, 0.1f))
			.andNotEqualTo(new Triple<String, Integer, Float>("s", 11, 0.1f))
			.andNotEqualTo(new Triple<String, Integer, Float>("string", 12, 0.1f))
			.andNotEqualTo(new Triple<String, Integer, Float>("string", 11, 0.2f))
			.andNotEqualTo(new Triple<String, String, String>("string", "string", "string"))
			.andNotEqualTo(new Triple<Integer, Integer, Integer>(11, 11, 11)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testZip() throws Exception {
		Triple<String, Integer, Float>[] zipped = Triple.zip(new String[] { "s1", "s2" }, new Integer[] { 1, 2 }, new Float[] { 0.1f, 0.2f });
		assertThat(zipped, arrayContaining(new Triple<>("s1", 1, 0.1f), new Triple<>("s2", 2, 0.2f)));
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
