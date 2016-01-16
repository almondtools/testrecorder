package com.almondtools.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.almondtools.testrecorder.ConfigRegistry;
import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.TestGenerator;
import com.almondtools.testrecorder.util.Instrumented;
import com.almondtools.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "com.almondtools.testrecorder.scenarios.Imports", "com.almondtools.testrecorder.scenarios.Imports$List" })
public class ImportsTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		Imports object = new Imports("name");

		assertThat(object.toString(), equalTo("[name]:name"));

		TestGenerator testGenerator = TestGenerator.fromRecorded(object);
		assertThat(testGenerator.renderTest(Imports.class), compiles());
		assertThat(testGenerator.renderTest(Imports.class), testsRuns());
	}

	@Test
	public void testCode() throws Exception {
		Imports object = new Imports("name");

		assertThat(object.toString(), equalTo("[name]:name"));

		TestGenerator testGenerator = TestGenerator.fromRecorded(object);
		assertThat(testGenerator.testsFor(Imports.class), hasSize(1));
		assertThat(testGenerator.testsFor(Imports.class), contains(allOf(
			containsPattern("Imports imports? = new GenericObject() {*"
				+ "List list = *"
				+ "com.almondtools.testrecorder.scenarios.Imports.List otherList *"
				+ "}.as(Imports.class);"),
			containsPattern("new GenericMatcher() {*"
				+ "Matcher<List> list = new GenericMatcher() {*"
				+ "*"
				+ "}.matching(clazz(\"java.util.Arrays$ArrayList\"), List.class);*"
				+ "Matcher<com.almondtools.testrecorder.scenarios.Imports.List> otherList = new GenericMatcher() {*"
				+ "String name = \"name\";*"
				+ "}.matching(com.almondtools.testrecorder.scenarios.Imports.List.class);*"
				+ "}.matching(Imports.class)"))));
		assertThat(testGenerator.renderTest(Imports.class), allOf(
			containsString("import java.util.List;"),
			not(containsPattern("import com.almondtools.testrecorder.scenarios.Imports.List;"))));
	}
}