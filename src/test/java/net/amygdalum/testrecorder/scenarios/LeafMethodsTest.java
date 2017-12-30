package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.LeafMethods", "net.amygdalum.testrecorder.scenarios.LeafType" })
public class LeafMethodsTest {

	

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