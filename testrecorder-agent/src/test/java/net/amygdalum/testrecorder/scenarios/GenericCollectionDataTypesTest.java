package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GenericCollectionDataTypes" })
public class GenericCollectionDataTypesTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		List<BigInteger> bigInts = new ArrayList<>();
		List<BigDecimal> bigDecs = new ArrayList<>();

		GenericCollectionDataTypes dataTypes = new GenericCollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.bigIntegerLists(bigInts);
			dataTypes.bigDecimalLists(bigDecs);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericCollectionDataTypes.class)).hasSize(20);
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		List<BigInteger> bigInts = new ArrayList<>();

		GenericCollectionDataTypes dataTypes = new GenericCollectionDataTypes();
		dataTypes.bigIntegerLists(bigInts);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericCollectionDataTypes.class)).hasSize(1);
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class).getTestCode())
			.contains("list1, containsInOrder(BigInteger.class, equalTo(new BigInteger(\"1\")))")
			.contains("list2, containsInOrder(BigInteger.class, equalTo(new BigInteger(\"1\")))");
	}
}