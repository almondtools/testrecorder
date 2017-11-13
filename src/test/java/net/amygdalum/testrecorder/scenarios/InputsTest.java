package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.Inputs"})
public class InputsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilableNotRecorded() throws Exception {
		Inputs in = new Inputs();
		in.notrecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Inputs.class), empty());
	}
	
	@Test
	public void testCompilable() throws Exception {
		Inputs in = new Inputs();
		in.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), compiles(Inputs.class));
	}
	
	@Test
	public void testCompilableConditionalReturn() throws Exception {
		Inputs in = new Inputs();
		in.recordedWithConditionalReturns();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), compiles(Inputs.class));
	}
	
	@Test
	public void testPrimitivesCompilable() throws Exception {
		Inputs in = new Inputs();
		in.primitivesRecorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), compiles(Inputs.class));
	}
	
	@Test
	public void testSideEffectsCompilable() throws Exception {
		Inputs in = new Inputs();
		in.sideEffectsRecorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), compiles(Inputs.class));
	}
	
	@Test
	public void testObjectSideEffectsCompilable() throws Exception {
		Inputs in = new Inputs();
		in.objectSideEffectsRecorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), compiles(Inputs.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		Inputs in = new Inputs();
		in.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(Inputs.class), allOf(
        	containsPattern("new FakeIn<String>(Inputs.class, \"read\", new Class[0])"),
            containsPattern(".add(\"net.amygdalum.testrecorder.scenarios.Inputs.recorded\", \"Hello\")"),
            containsPattern(".add(\"net.amygdalum.testrecorder.scenarios.Inputs.recorded\", \" \")"),
            containsPattern(".add(\"net.amygdalum.testrecorder.scenarios.Inputs.recorded\", \"World\")")
            ));
		assertThat(testGenerator.renderTest(Inputs.class), testsRun(Inputs.class));
	}
	
	@Test
	public void testRunnableConditionalReturns() throws Exception {
		Inputs in = new Inputs();
		in.recordedWithConditionalReturns();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), allOf(
			containsPattern("new FakeIn<String>(Inputs.class, \"conditionalReturnRead\", new Class[0])"),
            containsPattern(".add(\"net.amygdalum.testrecorder.scenarios.Inputs.recordedWithConditionalReturns\", \"Hello\")"),
			containsPattern(".add(\"net.amygdalum.testrecorder.scenarios.Inputs.recordedWithConditionalReturns\", \"World\")")
			));
		assertThat(testGenerator.renderTest(Inputs.class), testsRun(Inputs.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPrimitivesRunnable() throws Exception {
		Inputs in = new Inputs();
		in.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), allOf(
        	containsPattern("new FakeIn<Boolean>(Inputs.class, \"readBoolean\", new Class[0])"),
        	containsPattern("new FakeIn<Byte>(Inputs.class, \"readByte\", new Class[0])"),
        	containsPattern("new FakeIn<Short>(Inputs.class, \"readShort\", new Class[0])"),
        	containsPattern("new FakeIn<Integer>(Inputs.class, \"readInt\", new Class[0])"),
        	containsPattern("new FakeIn<Long>(Inputs.class, \"readLong\", new Class[0])"),
        	containsPattern("new FakeIn<Float>(Inputs.class, \"readFloat\", new Class[0])"),
        	containsPattern("new FakeIn<Double>(Inputs.class, \"readDouble\", new Class[0])"),
        	containsPattern("new FakeIn<Character>(Inputs.class, \"readChar\", new Class[0])")
            ));
		assertThat(testGenerator.renderTest(Inputs.class), testsRun(Inputs.class));
	}
	
	@Test
	public void testSideEffectsRunnable() throws Exception {
		Inputs in = new Inputs();
		in.sideEffectsRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), testsRun(Inputs.class));
	}
	
	@Test
	public void testObjectSideEffectsRunnable() throws Exception {
		Inputs in = new Inputs();
		in.objectSideEffectsRecorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), testsRun(Inputs.class));
	}
	
}