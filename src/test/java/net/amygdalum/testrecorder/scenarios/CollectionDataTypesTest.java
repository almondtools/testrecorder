package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

import net.amygdalum.testrecorder.TestGenerator;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.CollectionDataTypes"})
public class CollectionDataTypesTest {

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
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(30));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRuns());
	}
}