package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.HiddenInnerClass" })
public class HiddenInnerClassTest {

	

	@Test
	public void testCompilable() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");

		assertThat(object.toString()).isEqualTo("hidden name");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInnerClass.class), compiles(HiddenInnerClass.class));
		assertThat(testGenerator.renderTest(HiddenInnerClass.class), testsRun(HiddenInnerClass.class));
	}

	@Test
	public void testCode() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");

		assertThat(object.toString()).isEqualTo("hidden name");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(HiddenInnerClass.class)).hasSize(1);
		assertThat(testGenerator.testsFor(HiddenInnerClass.class), hasItem(
			containsPattern("Object hidden? = *new GenericObject() {*String name = \"hidden name\";*}.as(clazz(\"net.amygdalum.testrecorder.scenarios.HiddenInnerClass$Hidden\")).value();")));
        assertThat(testGenerator.testsFor(HiddenInnerClass.class), hasItem(
			containsPattern("HiddenInnerClass hiddenInnerClass? = new GenericObject() {*Object o = hidden2;*}.as(HiddenInnerClass.class)")));
        assertThat(testGenerator.testsFor(HiddenInnerClass.class), hasItem(
			containsPattern("new GenericMatcher() {*"
				+ "Matcher<?> o = new GenericMatcher() {*"
				+ "String name = \"hidden name\";*"
				+ "}.matching(clazz(\"net.amygdalum.testrecorder.scenarios.HiddenInnerClass$Hidden\"), Object.class);*"
				+ "}"
				+ ".matching(HiddenInnerClass.class));")));
	}
}