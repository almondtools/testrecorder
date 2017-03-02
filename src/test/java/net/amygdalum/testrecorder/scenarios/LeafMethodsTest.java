package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.LeafMethods", "net.amygdalum.testrecorder.scenarios.LeafType" })
public class LeafMethodsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		LeafMethods leafMethods = new LeafMethods();
		leafMethods.init(new LeafType(leafMethods));

		assertThat(leafMethods.method(), containsPattern("'net.amygdalum.testrecorder.scenarios.LeafMethods@*'"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(LeafType.class), compiles(LeafType.class));
		assertThat(testGenerator.renderTest(LeafType.class), testsRun(LeafType.class));
	}

	@Test
	public void testCode() throws Exception {
		LeafMethods leafMethods = new LeafMethods();
		leafMethods.init(new LeafType(leafMethods));

		assertThat(leafMethods.method(), containsPattern("'net.amygdalum.testrecorder.scenarios.LeafMethods@*'"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LeafType.class), hasSize(1));
	}

}