package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GenericConstructor" })
public class GenericConstructorTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testArrayListCompilable() throws Exception {
		List<String> list = new ArrayList<>(asList("A","B"));

		GenericConstructor object = new GenericConstructor(list);
		for (String value : asList("C","D")) {
			object.add(value);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericConstructor.class), hasSize(2));
		assertThat(testGenerator.renderTest(GenericConstructor.class), compiles(CollectionDataTypes.class));
		assertThat(testGenerator.renderTest(GenericConstructor.class), testsRun(CollectionDataTypes.class));
	}

}