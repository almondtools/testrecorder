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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.ConfigRegistry;
import net.amygdalum.testrecorder.DefaultConfig;
import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.CollectionsDecorators" })
public class CollectionsDecoratorsTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}

	@Test
	public void testUnmodifiableListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.unmodifiableList(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testSynchronizedListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.synchronizedList(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testCheckedListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.checkedList(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testEmptyListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.emptyList();

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testSingletonListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.singletonList("FooBar");

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testUnmodifiableSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.unmodifiableSet(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testSynchronizedSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.synchronizedSet(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testCheckedSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.checkedSet(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testEmptySetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.emptySet();

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testSingletonSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.singletonSet("FooBar");

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testUnmodifiableMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.unmodifiableMap(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testSynchronizedMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.synchronizedMap(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testCheckedMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.checkedMap(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testEmptyMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.emptyMap();

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

	@Test
	public void testSingletonMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.singletonMap("Foo", "Bar");

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.testsFor(CollectionsDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), compiles());
		assertThat(testGenerator.renderTest(CollectionsDecorators.class), testsRuns());
	}

}