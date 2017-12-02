package net.amygdalum.testrecorder.ioscenarios;

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
import net.amygdalum.testrecorder.util.Debug;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.ioscenarios.Inputs"})
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
        assertThat(Debug.print(testGenerator.renderTest(Inputs.class)), allOf(
        	containsPattern("FakeIO.fake(Inputs.class)"),
           	containsPattern(".fakeInput(new Aspect() {*public String read() {*}*})"),
            containsPattern(".add(Inputs.class, \"recorded\", *, \"Hello\")"),
            containsPattern(".add(Inputs.class, \"recorded\", *, \" \")"),
            containsPattern(".add(Inputs.class, \"recorded\", *, \"World\")")
            ));
		assertThat(testGenerator.renderTest(Inputs.class), testsRun(Inputs.class));
	}
	
	@Test
	public void testRunnableConditionalReturns() throws Exception {
		Inputs in = new Inputs();
		in.recordedWithConditionalReturns();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class), allOf(
			containsPattern("FakeIO.fake(Inputs.class)"),
           	containsPattern(".fakeInput(new Aspect() {*public String conditionalReturnRead() {*}*})"
           		+ ".add(Inputs.class, \"recordedWithConditionalReturns\", *, \"Hello\")"
           		+ ".add(Inputs.class, \"recordedWithConditionalReturns\", *, \"World\")")
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
        	containsPattern("FakeIO.fake(Inputs.class)"),
           	containsPattern(".fakeInput(new Aspect() {*public boolean readBoolean() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, true)"),
           	containsPattern(".fakeInput(new Aspect() {*public byte readByte() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, (byte) 42)"),
           	containsPattern(".fakeInput(new Aspect() {*public short readShort() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, (short) 42)"),
           	containsPattern(".fakeInput(new Aspect() {*public int readInt() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, 42)"),
           	containsPattern(".fakeInput(new Aspect() {*public long readLong() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, 42l)"),
           	containsPattern(".fakeInput(new Aspect() {*public float readFloat() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, 42.0f)"),
           	containsPattern(".fakeInput(new Aspect() {*public double readDouble() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, 42.0)"),
           	containsPattern(".fakeInput(new Aspect() {*public char readChar() {*}*})"
           		+ ".add(Inputs.class, \"primitivesRecorded\", *, 'a')")
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