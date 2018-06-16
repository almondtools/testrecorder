package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.Distinct.distinct;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class DistinctTest {

	@Test
	void testDistinctOnDistinctObjects() throws Exception {
		assertThat(Stream.of(new String("a"), new String("b")).filter(distinct()).count()).isEqualTo(2);
		assertThat(Stream.of(new String("a"), new String("a")).filter(distinct()).count()).isEqualTo(2);
	}

	@Test
	void testDistinctOnSameObjects() throws Exception {
		String same = "same";

		assertThat(Stream.of(same, same).filter(distinct()).count()).isEqualTo(1);
	}
}
