package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.almondtools.conmatch.strings.WildcardStringMatcher;

import net.amygdalum.testrecorder.CodeSerializer;
import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={
	"net.amygdalum.testrecorder.scenarios.DifferentDeclarationTypes",
	"net.amygdalum.testrecorder.scenarios.MyEnum",
	"net.amygdalum.testrecorder.scenarios.MyExtendedEnum",
	"net.amygdalum.testrecorder.scenarios.MyAnnotation",
	"net.amygdalum.testrecorder.scenarios.MyInterface",
	"net.amygdalum.testrecorder.scenarios.MyClass",
	"net.amygdalum.testrecorder.scenarios.MySingletonClass"})
public class DifferentDeclarationTypesTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		DifferentDeclarationTypes types = new DifferentDeclarationTypes();
		
		types.test();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DifferentDeclarationTypes.class), compiles(DifferentDeclarationTypes.class));
		assertThat(testGenerator.renderTest(DifferentDeclarationTypes.class), testsRun(DifferentDeclarationTypes.class));
	}

	@Test
	public void testCodeClass() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(new MyClass()), containsString("myClass1 = new MyClass()"));
	}

	@Test
	public void testCodeSingletonClass() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(MySingletonClass.SINGLE), WildcardStringMatcher.containsPattern("mySingletonClass2 = new GenericObject() {*"
			+ "}.as(MySingletonClass.class)"));
	}

	@Test
	public void testCodeEnum() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(MyEnum.VALUE1), containsString("serializedEnum1 = MyEnum.VALUE1"));
	}

	@Test
	public void testCodeExtendedEnum() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(MyExtendedEnum.VALUE1), containsString("serializedEnum1 = MyExtendedEnum.VALUE1"));
	}

}
