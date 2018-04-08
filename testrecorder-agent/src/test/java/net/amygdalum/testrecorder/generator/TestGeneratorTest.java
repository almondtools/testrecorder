package net.amygdalum.testrecorder.generator;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.ClassDescriptor;
import net.amygdalum.testrecorder.ContextSnapshot;
import net.amygdalum.testrecorder.MethodSignature;
import net.amygdalum.testrecorder.SnapshotManager;
import net.amygdalum.testrecorder.deserializers.TestComputationValueVisitor;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedOutput;

@ExtendWith(TemporaryFolderExtension.class)
public class TestGeneratorTest {

	private static SnapshotManager saveManager;

	private ExtensibleClassLoader loader;
	private AgentConfiguration config;
	private TestGenerator testGenerator;

	@BeforeAll
	public static void beforeClass() throws Exception {
		saveManager = SnapshotManager.MANAGER;
	}

	@AfterAll
	public static void afterClass() throws Exception {
		SnapshotManager.MANAGER = saveManager;
	}

	@BeforeEach
	public void before() throws Exception {
		loader = new ExtensibleClassLoader(TestGenerator.class.getClassLoader());
		config = defaultConfig().withLoader(loader);
		testGenerator = new TestGenerator(config);
	}

	@Test
	public void testAccept() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.testsFor(TestGeneratorTest.class))
			.hasSize(1)
			.anySatisfy(test -> {
				assertThat(test).containsSequence("int field = 12;",
					"intMethod(16);",
					"equalTo(22)",
					"int field = 8;");
			});
	}

	@Test
	public void testAcceptWithInitializer() throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer", "net.amygdalum.testrecorder.util.AgentInitializer".getBytes());
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode())
			.containsSequence(
				"@Before",
				"public void initialize() throws Exception {",
				"new AgentInitializer().run();",
				"}");
	}

	@Test
	public void testAcceptWithInput() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);
		snapshot.addInput(new SerializedInput(42, System.class, "currentTimeMillis", long.class, new Type[0]).updateResult(literal(42l)));

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode())
			.containsSequence(
				"@Before",
				"@After",
				"public void resetFakeIO() throws Exception {",
				"FakeIO.reset();",
				"}");
	}

	@Test
	public void testAcceptWithOutput() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);
		snapshot.addOutput(new SerializedOutput(42, Writer.class, "write", void.class, new Type[] { Writer.class }).updateArguments(literal("hello")));

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode())
			.containsSequence(
				"@Before",
				"@After",
				"public void resetFakeIO() throws Exception {",
				"FakeIO.reset();",
				"}");
	}

	@Test
	public void testSuppressesWarnings() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.renderTest(MyClass.class).getTestCode()).containsSequence("@SuppressWarnings(\"unused\")" + System.lineSeparator() + "public class");
	}

	@Test
	public void testSetSetup() throws Exception {
		testGenerator.setSetup(new TestComputationValueVisitor());
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.testsFor(TestGeneratorTest.class))
			.hasSize(1)
			.anySatisfy(test -> {
				assertThat(test).containsSequence("(net.amygdalum.testrecorder.generator.TestGeneratorTest$MyClass/",
					"int field: 12",
					"intMethod((16))",
					"equalTo(22)",
					"int field = 8;");
			});
	}

	@Test
	public void testSetMatcher() throws Exception {
		testGenerator.setMatcher(new TestComputationValueVisitor());
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.await();
		assertThat(testGenerator.testsFor(TestGeneratorTest.class))
			.hasSize(1)
			.anySatisfy(test -> {
				assertThat(test).containsSequence(
					"int field = 12;",
					"intMethod(16);",
					"(22)",
					"(net.amygdalum.testrecorder.generator.TestGeneratorTest$MyClass/",
					"int field: 8");
			});
	}

	@Test
	public void testTestsForEmpty() throws Exception {
		assertThat(testGenerator.testsFor(MyClass.class)).isEmpty();
	}

	@Test
	public void testTestsForAfterClear() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);
		testGenerator.accept(snapshot);

		testGenerator.clearResults();

		assertThat(testGenerator.testsFor(MyClass.class)).isEmpty();
	}

	@Test
	public void testRenderCode() throws Exception {
		ContextSnapshot snapshot1 = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot1.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot1.setSetupArgs(literal(int.class, 16));
		snapshot1.setSetupGlobals(new SerializedField[0]);
		snapshot1.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot1.setExpectArgs(literal(int.class, 16));
		snapshot1.setExpectResult(literal(int.class, 22));
		snapshot1.setExpectGlobals(new SerializedField[0]);
		ContextSnapshot snapshot2 = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot2.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 13))));
		snapshot2.setSetupArgs(literal(int.class, 17));
		snapshot2.setSetupGlobals(new SerializedField[0]);
		snapshot2.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 9))));
		snapshot2.setExpectArgs(literal(int.class, 17));
		snapshot2.setExpectResult(literal(int.class, 23));
		snapshot2.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot1);
		testGenerator.accept(snapshot2);

		testGenerator.await();
		assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode()).containsSequence(
			"int field = 12;",
			"intMethod(16);",
			"equalTo(22)",
			"int field = 8;",
			"int field = 13;",
			"intMethod(17);",
			"equalTo(23)",
			"int field = 9;");
	}

	@Test
	public void testComputeClassName() throws Exception {
		assertThat(testGenerator.computeClassName(ClassDescriptor.of(MyClass.class))).isEqualTo("MyClassRecordedTest");
	}

	@Test
	public void testWriteResults(TemporaryFolder folder) throws Exception {
		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.await();
		testGenerator.writeResults(folder.getRoot());

		assertThat(Files.exists(folder.resolve("net/amygdalum/testrecorder/generator/TestGeneratorTestRecordedTest.java"))).isTrue();
	}

	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new MethodSignature(declaringClass, new Annotation[0], resultType, methodName, new Annotation[0][0], argumentTypes));
	}

	private SerializedObject objectOf(Class<MyClass> type, SerializedField... fields) {
		SerializedObject setupThis = new SerializedObject(type);
		for (SerializedField field : fields) {
			setupThis.addField(field);
		}
		return setupThis;
	}

	@SuppressWarnings("unused")
	private static class MyClass {

		private int field;

		public int intMethod(int arg) {
			return field + arg;
		}
	}

}
