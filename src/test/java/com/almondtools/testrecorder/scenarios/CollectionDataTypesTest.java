package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.generator.TestGenerator;

public class CollectionDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.CollectionDataTypes");
	}
	
	@Test
	public void testCompilable() throws Exception {
		List<Integer> list = new ArrayList<>();
		Set<Integer> set = new HashSet<>();
		Map<Integer, Integer> map = new HashMap<>();
		
		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.lists(list, i);
			dataTypes.sets(set, i);
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRuns());
	}
}