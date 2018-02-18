package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static net.amygdalum.testrecorder.testing.assertj.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Debug;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

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
		assertThat(Debug.print(testGenerator.renderTest(GenericTypeInference.class))).satisfies(testsRun());
	}

}