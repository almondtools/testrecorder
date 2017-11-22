package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.CollectionDataTypes" })
public class CollectionDataTypesTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testArrayListCompilable() throws Exception {
		List<Integer> list = new ArrayList<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.lists(list, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testArrayListNullValuesCompilable() throws Exception {
		List<Integer> list = new ArrayList<>();
		list.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.lists(list, 0);
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testLinkedListCompilable() throws Exception {
		List<Integer> list = new LinkedList<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.lists(list, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testLinkedListNullValuesCompilable() throws Exception {
		List<Integer> list = new LinkedList<>();
		list.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.lists(list, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testHashSetCompilable() throws Exception {
		Set<Integer> set = new HashSet<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.sets(set, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testHashSetNullValuesCompilable() throws Exception {
		Set<Integer> set = new HashSet<>();
		set.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.sets(set, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testTreeSetCompilable() throws Exception {
		Set<Integer> set = new HashSet<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.sets(set, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testTreeSetNullValuesCompilable() throws Exception {
		Set<Integer> set = new HashSet<>();
		set.add(null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.sets(set, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testLinkedHashSetCompilable() throws Exception {
		Set<Integer> set = new LinkedHashSet<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.sets(set, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testHashMapCompilable() throws Exception {
		Map<Integer, Integer> map = new HashMap<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testHashMapNullValueCompilable() throws Exception {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(42, null);
		map.put(null, 42);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.maps(map, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testTreeMapCompilable() throws Exception {
		Map<Integer, Integer> map = new TreeMap<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testTreeMapNullValuesCompilable() throws Exception {
		Map<Integer, Integer> map = new TreeMap<>();
		map.put(42, null);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.maps(map, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testLinkedHashMapCompilable() throws Exception {
		Map<Integer, Integer> map = new LinkedHashMap<>();

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.maps(map, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionDataTypes.class), hasSize(10));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}

	@Test
	public void testLinkedHashMapNullValuesCompilable() throws Exception {
		Map<Integer, Integer> map = new LinkedHashMap<>();
		map.put(42, null);
		map.put(null, 42);

		CollectionDataTypes dataTypes = new CollectionDataTypes();
		dataTypes.maps(map, 0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(CollectionDataTypes.class), testsRun(CollectionDataTypes.class));
	}
}