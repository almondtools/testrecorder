package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FileSerializerTest {

	@Test
	public void testFileSerializer() throws Exception {
		assertThat(FileSerializer.class).satisfies(utilityClass().conventions());
	}

	@Test
	public void testStore() throws Exception {
		String stored = FileSerializer.store("target/temp", new byte[] { 1, 2 });
		assertThat(stored).containsWildcardPattern("*.serialized");
	}

	@Test
	public void testLoad() throws Exception {
		String stored = FileSerializer.store("target/temp", new byte[] { 1, 2 });
		byte[] loaded = FileSerializer.load("target/temp", stored, byte[].class);
		assertThat(loaded).contains((byte) 1, (byte) 2);
	}

}
