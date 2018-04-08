package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
	"net.amygdalum.testrecorder.scenarios.DifferentDeclarationTypes",
	"net.amygdalum.testrecorder.scenarios.MyEnum",
	"net.amygdalum.testrecorder.scenarios.MyExtendedEnum",
	"net.amygdalum.testrecorder.scenarios.MyAnnotation",
	"net.amygdalum.testrecorder.scenarios.MyInterface",
	"net.amygdalum.testrecorder.scenarios.MyClass",
	"net.amygdalum.testrecorder.scenarios.MySingletonClass" })
public class DifferentDeclarationTypesTest {

	@Test
	public void testCompilable() throws Exception {
		DifferentDeclarationTypes types = new DifferentDeclarationTypes();

		types.test();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DifferentDeclarationTypes.class)).satisfies(testsRun());
	}

	@Test
	public void testCodeClass() throws Exception {
		CodeSerializer codeSerializer = setupSerializer();

		assertThat(codeSerializer.serialize(new MyClass())).contains("myClass1 = new MyClass()");
	}

	@Test
	public void testCodeSingletonClass() throws Exception {
		CodeSerializer codeSerializer = setupSerializer();

		assertThat(codeSerializer.serialize(MySingletonClass.SINGLE)).containsWildcardPattern("mySingletonClass2 = new GenericObject() {*"
			+ "}.as(MySingletonClass.class)");
	}

	@Test
	public void testCodeEnum() throws Exception {
		CodeSerializer codeSerializer = setupSerializer();

		assertThat(codeSerializer.serialize(MyEnum.VALUE1)).contains("serializedEnum1 = MyEnum.VALUE1");
	}

	@Test
	public void testCodeExtendedEnum() throws Exception {
		CodeSerializer codeSerializer = setupSerializer();

		assertThat(codeSerializer.serialize(MyExtendedEnum.VALUE1)).contains("serializedEnum1 = MyExtendedEnum.VALUE1");
	}

	private static CodeSerializer setupSerializer() {
		return new CodeSerializer("net.amygdalum.testrecorder.scenarios");
	}

}
