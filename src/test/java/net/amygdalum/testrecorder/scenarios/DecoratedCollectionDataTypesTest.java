package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.DecoratedCollectionDataTypes"})
public class DecoratedCollectionDataTypesTest {

	@Test
	public void testListsCompilable() throws Exception {
		DecoratedCollectionDataTypes dataTypes = new DecoratedCollectionDataTypes();
		List<Object> base = new ArrayList<>();
		base.add("Hello");
		base.add("World");
		
		dataTypes.unmodifiableList(base);
		dataTypes.synchronizedList(base);
		dataTypes.checkedList(base);
		dataTypes.emptyList();
		dataTypes.singletonList("FooBar");

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.renderTest(DecoratedCollectionDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(DecoratedCollectionDataTypes.class), testsRuns());
	}
}