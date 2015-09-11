package com.almondtools.invivoderived.scenarios;

import static com.almondtools.invivoderived.SnapshotGenerator.setSnapshotConsumer;
import static com.almondtools.invivoderived.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.invivoderived.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.invivoderived.SnapshotInstrumentor;
import com.almondtools.invivoderived.generator.TestGenerator;

public class PrimitiveArrayDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	private TestGenerator testGenerator;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invivoderived.scenarios.PrimitiveArrayDataTypes");
	}

	@Before
	public void before() throws Exception {
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator);
	}

	@Test
	public void testCompilable() throws Exception {
		boolean[] booleans = new boolean[] { false, true, false, false, true, false, true, true };
		char[] chars = new char[] { Character.MIN_VALUE, 'a', 'b', 'c', 'd', 'e', Character.MAX_VALUE };
		byte[] bytes = new byte[] { 1, -11, 2, Byte.MAX_VALUE, Byte.MIN_VALUE, 0 };
		short[] shorts = new short[] { 1, -1, 1100, Short.MAX_VALUE, Short.MIN_VALUE, 0 };
		int[] integers = new int[] { 1, -13, -64000, Integer.MAX_VALUE, Integer.MIN_VALUE, 0 };
		float[] floats = new float[] { 1.0f, -13.0f, -0.64f, 0.15e-15f, Float.MAX_VALUE, Float.MIN_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN };
		long[] longs = new long[] { 1l, -17l, 942342334, Long.MAX_VALUE, Long.MIN_VALUE, 0 };
		double[] doubles = new double[] { 1.0, 17.5, -1.34444423242334243, 0.152348238479e15, Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN };

		PrimitiveArrayDataTypes dataTypes = new PrimitiveArrayDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.booleans(booleans);
			dataTypes.chars(chars);
			dataTypes.bytes(bytes);
			dataTypes.shorts(shorts);
			dataTypes.integers(integers);
			dataTypes.floats(floats);
			dataTypes.longs(longs);
			dataTypes.doubles(doubles);
		}
		assertThat(testGenerator.renderTest(PrimitiveArrayDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(PrimitiveArrayDataTypes.class), testsRuns());
	}
}