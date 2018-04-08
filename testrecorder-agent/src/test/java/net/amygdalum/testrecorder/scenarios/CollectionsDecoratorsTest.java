package net.amygdalum.testrecorder.scenarios;

import static java.util.Collections.checkedList;
import static java.util.Collections.checkedMap;
import static java.util.Collections.checkedSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;
import static java.util.Collections.synchronizedSet;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.CollectionsDecorators" })
public class CollectionsDecoratorsTest {

	@Test
	public void testUnmodifiableListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.unmodifiableList(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testSynchronizedListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.synchronizedList(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testCheckedListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.checkedList(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testEmptyListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.emptyList();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testSingletonListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.singletonList("FooBar");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testUnmodifiableSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.unmodifiableSet(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testSynchronizedSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.synchronizedSet(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testCheckedSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.checkedSet(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testEmptySetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.emptySet();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testSingletonSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.singletonSet("FooBar");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testUnmodifiableMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.unmodifiableMap(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testSynchronizedMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.synchronizedMap(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testCheckedMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.checkedMap(base);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testEmptyMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.emptyMap();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testSingletonMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.singletonMap("Foo", "Bar");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeUnmodifiableListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.consume(unmodifiableList(base));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeSynchronizedListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.consume(synchronizedList(base));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeCheckedListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		List<String> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");

		dataTypes.consume(checkedList(base, String.class));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeEmptyListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.consume(emptyList());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeSingletonListsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.consume(singletonList("FooBar"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeUnmodifiableSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.consume(unmodifiableSet(base));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeSynchronizedSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<Object> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.consume(synchronizedSet(base));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeCheckedSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Set<String> base = new HashSet<>();
		base.add("Hello");
		base.add("World");

		dataTypes.consume(checkedSet(base, String.class));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeEmptySetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.consume(emptySet());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeSingletonSetsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.consume(singleton("FooBar"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeUnmodifiableMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.consume(unmodifiableMap(base));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeSynchronizedMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<Object, Object> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.consume(synchronizedMap(base));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeCheckedMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();
		Map<String, String> base = new HashMap<>();
		base.put("Hello", "World");
		base.put("Foo", "Bar");

		dataTypes.consume(checkedMap(base, String.class, String.class));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeEmptyMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.consume(emptyMap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testConsumeSingletonMapsCompilable() throws Exception {
		CollectionsDecorators dataTypes = new CollectionsDecorators();

		dataTypes.consume(singletonMap("Foo", "Bar"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CollectionsDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(CollectionsDecorators.class)).satisfies(testsRun());
	}

}