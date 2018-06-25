package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.Distinct.distinct;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;
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

	@Test
	void testDistinctOnNull() throws Exception {
		assertThat(Stream.of((Object) null).filter(distinct()).count()).isEqualTo(1);
		assertThat(Stream.of((Object) null, (Object) null).filter(distinct()).count()).isEqualTo(1);
	}
	
	@Test
	void testDistinctOnLargeStream() throws Exception {
		assertThat(IntStream.range(-200, 200)
			.mapToObj(Integer::valueOf)
			.filter(distinct())
			.count()).isEqualTo(400);
		
		assertThat(Stream.concat(
			IntStream.range(-200, 200).mapToObj(Integer::valueOf), 
			IntStream.range(-200, 200).mapToObj(Integer::valueOf))
			.filter(distinct())
			.count()).withFailMessage("expecting all byte integers to be same, all non byte integers to be different/distinct").isEqualTo(400 + 400 - 256);
	}
	
}
