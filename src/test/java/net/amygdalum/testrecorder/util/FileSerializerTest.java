package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.byteArrayContaining;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.conmatch.strings.WildcardStringMatcher;

public class FileSerializerTest {

    @Test
    public void testFileSerializer() throws Exception {
        assertThat(FileSerializer.class, isUtilityClass());
    }

    @Test
    public void testStore() throws Exception {
        String stored = FileSerializer.store("target/temp", new byte[]{1,2});
        assertThat(stored, WildcardStringMatcher.containsPattern("*.serialized"));
    }

    @Test
    public void testLoad() throws Exception {
        String stored = FileSerializer.store("target/temp", new byte[]{1,2});
        byte[] loaded = FileSerializer.load("target/temp", stored, byte[].class);
        assertThat(loaded, byteArrayContaining((byte)1,(byte) 2));
    }

}
