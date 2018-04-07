package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GenericTypeInference" })
public class GenericTypeInferenceTest {

	@Test
	public void testValueUsedInGeneric1CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		Map<String, Object> map = new HashMap<>();
		BigDecimal value = new BigDecimal("4.2");
		map.put("key", value);

		boolean containing = bean.containsValue(map, value);

		assertThat(containing).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@Test
	public void testValueUsedInGeneric2CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		Map<Object, String> map = new HashMap<>();
		BigDecimal key = new BigDecimal("4.2");
		map.put(key, key.toString());

		String value = bean.getValue(map, key);

		assertThat(value).isEqualTo("4.2");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@Test
	public void testMapDeepTypeInference1CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		Map<String, List<Object>> map = new HashMap<>();

		boolean value = bean.addValue(map, "key", "value");

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@Test
	public void testMapDeepTypeInference2CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		Map<String, List<Object>> map = new HashMap<>();
		map.put("key", synchronizedList(new ArrayList<>(asList("oldvalue"))));

		boolean value = bean.addValue(map, "key", "value");

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMapDeepTypeInference3CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		Map<String, List<Object>> map = new HashMap<>();
		List<String> list = synchronizedList(new ArrayList<>(asList("oldvalue")));
		map.put("key", (List) list);

		boolean value = bean.removeValue(map, "key", list);

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMapDeepTypeInference4CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		Map<String, List<Object>> map = new HashMap<>();
		List<String> list = synchronizedList(new ArrayList<>(asList("oldvalue")));
		map.put("key", (List) list);

		boolean value = bean.removeValue("key", list, map);

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@Test
	public void testMapDeepTypeInference5CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		List<Set<String>> lists = new ArrayList<>();
		lists.add(Collections.singleton("value"));

		boolean value = bean.contains(lists, "value");

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@Test
	public void testMapDeepTypeInference6CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		List<List<Object>> lists = new ArrayList<>();
		lists.add(synchronizedList(new ArrayList<>(asList("oldvalue"))));

		boolean value = bean.addValueToAll(lists, "value");

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMapDeepTypeInference7CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		List<List<Object>> lists = new ArrayList<>();
		List<String> list = synchronizedList(new ArrayList<>(asList("oldvalue")));
		lists.add((List) list);

		boolean value = bean.removeFromAll(lists, list);

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMapDeepTypeInference8CompilesAndRuns() throws Exception {
		GenericTypeInference bean = new GenericTypeInference();
		List<List<Object>> lists = new ArrayList<>();
		List<String> list = synchronizedList(new ArrayList<>(asList("oldvalue")));
		lists.add((List) list);

		boolean value = bean.removeFromAllInverse(list, lists);

		assertThat(value).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericTypeInference.class)).satisfies(testsRun());
	}

}