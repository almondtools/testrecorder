package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.CollectionDataTypes" })
public class CollectionDataTypesTest {

	@Test
	public void testArrayListCompilesAndRuns() throws Exception {
		List<Integer> list = new ArrayList<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.lists(list, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testArrayListNullValuesCompilesAndRuns() throws Exception {
		List<Integer> list = new ArrayList<>();
		list.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.lists(list, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testLinkedListCompilesAndRuns() throws Exception {
		List<Integer> list = new LinkedList<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.lists(list, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testLinkedListNullValuesCompilesAndRuns() throws Exception {
		List<Integer> list = new LinkedList<>();
		list.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.lists(list, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testHashSetCompilesAndRuns() throws Exception {
		Set<Integer> set = new HashSet<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.sets(set, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testHashSetNullValuesCompilesAndRuns() throws Exception {
		Set<Integer> set = new HashSet<>();
		set.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.sets(set, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testTreeSetCompilesAndRuns() throws Exception {
		Set<Integer> set = new HashSet<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.sets(set, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testTreeSetNullValuesCompilesAndRuns() throws Exception {
		Set<Integer> set = new HashSet<>();
		set.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.sets(set, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testLinkedHashSetCompilesAndRuns() throws Exception {
		Set<Integer> set = new LinkedHashSet<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.sets(set, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testHashMapCompilesAndRuns() throws Exception {
		Map<Integer, Integer> map = new HashMap<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testHashMapNullValueCompilesAndRuns() throws Exception {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(42, null);
		map.put(null, 42);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.maps(map, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testTreeMapCompilesAndRuns() throws Exception {
		Map<Integer, Integer> map = new TreeMap<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testTreeMapNullValuesCompilesAndRuns() throws Exception {
		Map<Integer, Integer> map = new TreeMap<>();
		map.put(42, null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.maps(map, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testLinkedHashMapCompilesAndRuns() throws Exception {
		Map<Integer, Integer> map = new LinkedHashMap<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class)).hasSize(10);
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testLinkedHashMapNullValuesCompilesAndRuns() throws Exception {
		Map<Integer, Integer> map = new LinkedHashMap<>();
		map.put(42, null);
		map.put(null, 42);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.maps(map, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class)).satisfies(testsRun());
	}
}