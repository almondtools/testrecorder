package net.amygdalum.testrecorder.generator;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.SnapshotManager;
import net.amygdalum.testrecorder.TestAgentConfiguration;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedInput;
import net.amygdalum.testrecorder.types.SerializedOutput;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.types.VirtualMethodSignature;
import net.amygdalum.testrecorder.util.ClassDescriptor;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.xrayinterface.XRayInterface;

@ExtendWith(TemporaryFolderExtension.class)
public class TestGeneratorTest {

	private static SnapshotManager saveManager;

	private ExtensibleClassLoader loader;
	private TestAgentConfiguration config;
	private TestGenerator testGenerator;

	@BeforeAll
	static void beforeClass() throws Exception {
		saveManager = SnapshotManager.MANAGER;
	}

	@AfterAll
	static void afterClass() throws Exception {
		SnapshotManager.MANAGER = saveManager;
	}

	@BeforeEach
	void before() throws Exception {
		loader = new ExtensibleClassLoader(TestGenerator.class.getClassLoader());
		config = defaultConfig().withLoader(loader);
		testGenerator = new TestGenerator(config);
	}

	@Test
	void testTestGenerator() throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.generator.TestGeneratorTest$InvalidTestTemplateTestGeneratorProfile".getBytes());
		config.reset();
		assertThatCode(() -> new TestGenerator(config))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void testReload() throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.PerformanceProfile", "net.amygdalum.testrecorder.generator.TestGeneratorTest$CustomPerformanceProfile".getBytes());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.generator.TestGeneratorTest$Profile".getBytes());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.deserializers.builder.SetupGenerator", "".getBytes());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator", "".getBytes());
		config.reset();

		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);
		snapshot.addInput(new SerializedInput(42, new MethodSignature(System.class, long.class, "currentTimeMillis", new Type[0])).updateResult(literal(42l)));
		snapshot.addOutput(new SerializedOutput(42, new MethodSignature(Writer.class, void.class, "write", new Type[] {Writer.class})).updateArguments(literal("hello")));

		testGenerator.reload(config);

		ClassGenerator gen = testGenerator.generatorFor(ClassDescriptor.of(MyClass.class));
		gen.generate(snapshot);
		assertThat(gen.render()).containsWildcardPattern("Test*resetFakeIO:setup*test");
	}

	@Nested
	class testAccept {
		@Test
		void onCommon() throws Exception {
			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.accept(snapshot);

			testGenerator.await();
			assertThat(testGenerator.testsFor(TestGeneratorTest.class))
				.hasSize(1)
				.anySatisfy(test -> {
					assertThat(test).containsSubsequence("int field = 12;",
						"intMethod(16);",
						"equalTo(22)",
						"int field = 8;");
				});
		}

		@Test
		void withInput() throws Exception {
			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);
			snapshot.addInput(new SerializedInput(42, new MethodSignature(System.class, long.class, "currentTimeMillis", new Type[0])).updateResult(literal(42l)));

			testGenerator.accept(snapshot);

			testGenerator.await();
			assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode())
				.containsSubsequence(
					"@Before",
					"@After",
					"public void resetFakeIO() throws Exception {",
					"FakeIO.reset();",
					"}");
		}

		@Test
		void withOutput() throws Exception {
			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);
			snapshot.addOutput(new SerializedOutput(42, new MethodSignature(Writer.class, void.class, "write", new Type[] {Writer.class})).updateArguments(literal("hello")));

			testGenerator.accept(snapshot);

			testGenerator.await();
			assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode())
				.containsSubsequence(
					"@Before",
					"@After",
					"public void resetFakeIO() throws Exception {",
					"FakeIO.reset();",
					"}");
		}

		@Test
		void suppressingWarnings() throws Exception {
			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.accept(snapshot);

			testGenerator.await();
			assertThat(testGenerator.renderTest(MyClass.class).getTestCode()).containsSubsequence("@SuppressWarnings(\"unused\")" + System.lineSeparator() + "public class");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		void withExceptionIsLogging(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ContextSnapshot snapshot = new ContextSnapshot(0, "key", new VirtualMethodSignature(new MethodSignature(String.class, String.class, "toString", new Class[0]))) {
				@Override
				public Type getThisType() {
					throw new RuntimeException("Message for RuntimeException");
				}
			};
			snapshot.setSetupThis(literal(String.class, "astring"));
			snapshot.setSetupArgs();
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(literal(String.class, "astring"));
			snapshot.setExpectArgs();
			snapshot.setExpectResult(literal(String.class, "astring"));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.accept(snapshot);
			testGenerator.await();
			assertThat(error.toString()).contains("Message for RuntimeException");
		}
	}

	@Nested
	class testTestsFor {
		@Test
		void onEmpty() throws Exception {
			assertThat(testGenerator.testsFor(MyClass.class)).isEmpty();
		}

		@Test
		void afterClear() throws Exception {
			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);
			testGenerator.accept(snapshot);

			testGenerator.clearResults();

			assertThat(testGenerator.testsFor(MyClass.class)).isEmpty();
		}
	}

	@Test
	void testRenderCode() throws Exception {
		FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
		ContextSnapshot snapshot1 = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot1.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
		snapshot1.setSetupArgs(literal(int.class, 16));
		snapshot1.setSetupGlobals(new SerializedField[0]);
		snapshot1.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
		snapshot1.setExpectArgs(literal(int.class, 16));
		snapshot1.setExpectResult(literal(int.class, 22));
		snapshot1.setExpectGlobals(new SerializedField[0]);
		ContextSnapshot snapshot2 = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot2.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 13))));
		snapshot2.setSetupArgs(literal(int.class, 17));
		snapshot2.setSetupGlobals(new SerializedField[0]);
		snapshot2.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 9))));
		snapshot2.setExpectArgs(literal(int.class, 17));
		snapshot2.setExpectResult(literal(int.class, 23));
		snapshot2.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot1);
		testGenerator.accept(snapshot2);

		testGenerator.await();
		assertThat(testGenerator.renderTest(TestGeneratorTest.class).getTestCode()).containsSubsequence(
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
	void testComputeClassName() throws Exception {
		assertThat(testGenerator.computeClassName(ClassDescriptor.of(MyClass.class))).isEqualTo("MyClassRecordedTest");
	}

	@Nested
	class testWriteResults {
		@Test
		void withOrdinaryPackage(TemporaryFolder folder) throws Exception {
			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.accept(snapshot);

			testGenerator.await();
			testGenerator.writeResults(folder.getRoot());

			assertThat(Files.exists(folder.resolve("net/amygdalum/testrecorder/generator/TestGeneratorTestRecordedTest.java"))).isTrue();
		}

		@Test
		void withProtectedPackage(TemporaryFolder folder) throws Exception {
			ContextSnapshot snapshot = contextSnapshot(String.class, String.class, "toString");
			snapshot.setSetupThis(literal(String.class, "astring"));
			snapshot.setSetupArgs();
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(literal(String.class, "astring"));
			snapshot.setExpectArgs();
			snapshot.setExpectResult(literal(String.class, "astring"));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.accept(snapshot);

			testGenerator.await();
			testGenerator.writeResults(folder.getRoot());

			assertThat(Files.exists(folder.resolve("test/java/lang/StringRecordedTest.java"))).isTrue();
		}
	}

	@Test
	public void testFromRecorded() throws Exception {
		XRayInterface.xray(SnapshotManager.init(config)).to(OpenSnapshotManager.class).setSnapshotConsumer(null);
		assertThat(TestGenerator.fromRecorded()).isNull();
	}

	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new VirtualMethodSignature(new MethodSignature(declaringClass, resultType, methodName, argumentTypes)));
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

	public static class CustomPerformanceProfile implements PerformanceProfile {

		@Override
		public long getTimeoutInMillis() {
			return 0;
		}

		@Override
		public long getIdleTime() {
			return 0;
		}
	}

	public static class Profile implements TestGeneratorProfile {

		@Override
		public List<CustomAnnotation> annotations() {
			return emptyList();
		}

		@Override
		public Class<? extends TestTemplate> template() {
			return CustomTemplate.class;
		}

	}

	public static class CustomTemplate implements TestTemplate {

		@Override
		public Class<?>[] getTypes() {
			return new Class[0];
		}

		@Override
		public String testClass(String methodName, TypeManager types, Map<String, String> setups, Set<String> tests) {
			return "Test\n"
				+ setups.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(joining("\n", "\n", "\n"))
				+ tests.stream().collect(joining("\n", "\n", "\n"));
		}

		@Override
		public String setupMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements) {
			return "setup";
		}

		@Override
		public String testMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements) {
			return "test";
		}

	}

	public static class InvalidTestTemplateTestGeneratorProfile implements TestGeneratorProfile {

		@Override
		public List<CustomAnnotation> annotations() {
			return emptyList();
		}

		@Override
		public Class<? extends TestTemplate> template() {
			return InvalidTestTemplate.class;
		}

	}

	public static class InvalidTestTemplate implements TestTemplate {

		InvalidTestTemplate(String s) {
			//constructor with arguments to suppress auto constructor
		}

		@Override
		public Class<?>[] getTypes() {
			return new Class[0];
		}

		@Override
		public String testClass(String methodName, TypeManager types, Map<String, String> setups, Set<String> tests) {
			return "class";
		}

		@Override
		public String setupMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements) {
			return "setup";
		}

		@Override
		public String testMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements) {
			return "test";
		}

	}

	interface OpenSnapshotManager {

		void setSnapshotConsumer(SnapshotConsumer s);

	}

}
