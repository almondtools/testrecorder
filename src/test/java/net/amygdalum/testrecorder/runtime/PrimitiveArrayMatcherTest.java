package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.booleanArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.byteArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.charArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.doubleArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.floatArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.intArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.longArrayContaining;
import static net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher.shortArrayContaining;
import static org.assertj.core.api.Assertions.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class PrimitiveArrayMatcherTest {

    @Test
    public void testMatchesSafelyOnTypeMismatch() throws Exception {
        assertThat(booleanArrayContaining().matches(new Object())).isFalse();
        assertThat(booleanArrayContaining().matches(new Object[0])).isFalse();
        assertThat(booleanArrayContaining().matches(new int[0])).isFalse();
        assertThat(booleanArrayContaining().matches(new Integer[0])).isFalse();
    }

    @Test
    public void testMatchesSafelyOnBoolean() throws Exception {
        assertThat(booleanArrayContaining(true, false).matches(new boolean[] { true, false })).isTrue();
        assertThat(booleanArrayContaining(true, false).matches(new boolean[] { true })).isFalse();
        assertThat(booleanArrayContaining(true, false).matches(new boolean[] { false, true })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnChar() throws Exception {
        assertThat(charArrayContaining('a', 'z').matches(new char[] { 'a', 'z' })).isTrue();
        assertThat(charArrayContaining('a', 'z').matches(new char[] { 'a' })).isFalse();
        assertThat(charArrayContaining('a', 'z').matches(new char[] { 'z', 'a' })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnByte() throws Exception {
        assertThat(byteArrayContaining(b(0), b(1)).matches(new byte[] { 0, 1 })).isTrue();
        assertThat(byteArrayContaining(b(0), b(1)).matches(new byte[] { 0 })).isFalse();
        assertThat(byteArrayContaining(b(0), b(1)).matches(new byte[] { 1, 0 })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnShort() throws Exception {
        assertThat(shortArrayContaining(s(1), s(0)).matches(new short[] { 1, 0 })).isTrue();
        assertThat(shortArrayContaining(s(1), s(0)).matches(new short[] { 1 })).isFalse();
        assertThat(shortArrayContaining(s(1), s(0)).matches(new short[] { 0, 1 })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnInt() throws Exception {
        assertThat(intArrayContaining(-9999999, 1).matches(new int[] { -9999999, 1 })).isTrue();
        assertThat(intArrayContaining(-9999999, 1).matches(new int[] { -9999999 })).isFalse();
        assertThat(intArrayContaining(-9999999, 1).matches(new int[] { 1, -9999999 })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnLong() throws Exception {
        assertThat(longArrayContaining(1, -9999999999l).matches(new long[] { 1, -9999999999l })).isTrue();
        assertThat(longArrayContaining(1, -9999999999l).matches(new long[] { 1 })).isFalse();
        assertThat(longArrayContaining(1, -9999999999l).matches(new long[] { -9999999999l, 1 })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnFloat() throws Exception {
        assertThat(floatArrayContaining(-0.1e-7f, 1.234f).matches(new float[] { -0.1e-7f, 1.234f })).isTrue();
        assertThat(floatArrayContaining(-0.1e-7f, 1.234f).matches(new float[] { -0.1e-7f })).isFalse();
        assertThat(floatArrayContaining(-0.1e-7f, 1.234f).matches(new float[] { 1.234f, -0.1e-7f })).isFalse();
    }

    @Test
    public void testMatchesSafelyOnDouble() throws Exception {
        assertThat(doubleArrayContaining(0.3e-33, -46232.83345435234235234235235235).matches(new double[] { 0.3e-33, -46232.83345435234235234235235235 })).isTrue();
        assertThat(doubleArrayContaining(0.3e-33, -46232.83345435234235234235235235).matches(new double[] { -46232.83345435234235234235235235, 3e-33 })).isFalse();
        assertThat(doubleArrayContaining(0.3e-33, -46232.83345435234235234235235235).matches(new double[] { -46232.83345435234235234235235235, 0.3e-33 })).isFalse();
    }

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();
        
        booleanArrayContaining(true, false).describeTo(description);
        
        assertThat(description.toString()).isEqualTo("an array containing values of type <boolean>: [<true>, <false>]");
    }

    @Test
    public void testDescribeMismatchSafelyOnNonArray() throws Exception {
        StringDescription description = new StringDescription();
        
        booleanArrayContaining().describeMismatch(new Object(), description);
        
        assertThat(description.toString()).isEqualTo("not an array");
    }

    @Test
    public void testDescribeMismatchSafelyOnNonPrimitiveArray() throws Exception {
        StringDescription description = new StringDescription();
        
        booleanArrayContaining().describeMismatch(new Object[0], description);
        
        assertThat(description.toString()).isEqualTo("not a primitive array");
    }

    @Test
    public void testDescribeMismatchSafelyOnWronglyTypedArray() throws Exception {
        StringDescription description = new StringDescription();
        
        booleanArrayContaining().describeMismatch(new int[0], description);
        
        assertThat(description.toString()).isEqualTo("of type <int[]>");
    }

    @Test
    public void testDescribeMismatchSafelyOnWrongValues() throws Exception {
        StringDescription description = new StringDescription();

        doubleArrayContaining(0.3e-33, -46232.83345435234235234235235235).describeMismatch(new double[] { 3e-33 }, description);
        
        assertThat(description.toString()).isEqualTo("with items [<3.0E-33>]");
    }

    private byte b(int i) {
        return (byte) i;
    }

    private short s(int i) {
        return (short) i;
    }


}
