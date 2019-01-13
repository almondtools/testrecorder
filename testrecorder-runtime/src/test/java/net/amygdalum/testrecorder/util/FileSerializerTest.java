package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FileSerializerTest {

	@Test
	void testStore() throws Exception {
		String stored = new FileSerializer("target/temp").store(new byte[] { 1, 2 });
		assertThat(stored).containsWildcardPattern("*.serialized");
	}

	@Test
	void testLoad() throws Exception {
		FileSerializer fileSerializer = new FileSerializer("target/temp");
		String stored = fileSerializer.store(new byte[] { 1, 2 });
		byte[] loaded = fileSerializer.load(stored, byte[].class);
		assertThat(loaded).contains((byte) 1, (byte) 2);
	}

}
