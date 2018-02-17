package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.testing.assertj.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.DependentArguments" })
public class DependentArgumentsTest {

	@Test
	public void testMapWithTooGenericValueCompilesAndRuns() throws Exception {
		DependentArguments bean = new DependentArguments();
		Map<String, Object> map = new HashMap<>();
		BigDecimal value = new BigDecimal("4.2");
		map.put("key", value);
		
		boolean containing = bean.containsValue(map, value);

		assertThat(containing).isTrue();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DependentArguments.class)).satisfies(testsRun());
	}

	@Test
	public void testMapWithTooGenericKeyCompilesAndRuns() throws Exception {
		DependentArguments bean = new DependentArguments();
		Map<Object, String> map = new HashMap<>();
		BigDecimal key = new BigDecimal("4.2");
		map.put(key, key.toString());
		
		String value = bean.getValue(map, key);

		assertThat(value).isEqualTo("4.2");
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DependentArguments.class)).satisfies(testsRun());
	}

}