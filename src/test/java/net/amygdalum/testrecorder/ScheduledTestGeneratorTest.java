package net.amygdalum.testrecorder;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;

import net.amygdalum.testrecorder.deserializers.TestComputationValueVisitor;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.xrayinterface.XRayInterface;

@EnableRuleMigrationSupport
public class ScheduledTestGeneratorTest {

	private static SnapshotManager saveManager;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private AgentConfiguration config;
	private ScheduledTestGenerator testGenerator;

	@BeforeAll
	public static void beforeClass() throws Exception {
		saveManager = SnapshotManager.MANAGER;
	}

	@AfterAll
	public static void afterClass() throws Exception {
		SnapshotManager.MANAGER = saveManager;
		shutdownHooks().entrySet().stream()
			.filter(e -> e.getKey().getName().equals("$generate-shutdown"))
			.map(e -> e.getValue())
			.findFirst()
			.ifPresent(shutdown -> {
				Runtime.getRuntime().removeShutdownHook(shutdown);
				XRayInterface.xray(ScheduledTestGenerator.class).to(OpenScheduledTestGenerator.class).setDumpOnShutDown(null);
			});
	}

	@BeforeEach
	public void before() throws Exception {
		XRayInterface.xray(ScheduledTestGenerator.class).to(OpenScheduledTestGenerator.class).setDumpOnShutDown(null);
		config = defaultConfig();
		testGenerator = new ScheduledTestGenerator(config)
			.withDumpMaximum(1);
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
		assertThat(testGenerator.testsFor(ScheduledTestGeneratorTest.class))
			.hasSize(1)
			.anySatisfy(test -> {
				assertThat(test)
					.containsSequence(
						"int field = 12;",
						"intMethod(16);",
						"equalTo(22)",
						"int field = 8;");
			});
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
		assertThat(testGenerator.testsFor(ScheduledTestGeneratorTest.class))
			.hasSize(1)
			.anySatisfy(test -> {
				assertThat(test).containsSequence(
					"(net.amygdalum.testrecorder.ScheduledTestGeneratorTest$MyClass/",
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
		assertThat(testGenerator.testsFor(ScheduledTestGeneratorTest.class))
			.hasSize(1)
			.anySatisfy(test -> {
				assertThat(test).containsSequence(
					"int field = 12;",
					"intMethod(16);",
					"(22)",
					"(net.amygdalum.testrecorder.ScheduledTestGeneratorTest$MyClass/",
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

		testGenerator.withDumpMaximum(2);
		testGenerator.accept(snapshot1);
		testGenerator.accept(snapshot2);

		testGenerator.await();
		assertThat(testGenerator.renderTest(ScheduledTestGeneratorTest.class).getTestCode()).containsSequence(
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
	public void testComputeClassNameWithTemplate() throws Exception {
		assertThat(testGenerator.withClassName("${class}Suffix").computeClassName(ClassDescriptor.of(MyClass.class))).isEqualTo("MyClassSuffix");
		assertThat(testGenerator.withClassName("${counter}Suffix").computeClassName(ClassDescriptor.of(MyClass.class))).isEqualTo("0Suffix");
		assertThat(testGenerator.withClassName("Prefix${millis}Suffix").computeClassName(ClassDescriptor.of(MyClass.class))).containsWildcardPattern("Prefix*Suffix");
	}

	@Test
	public void testWriteResults() throws Exception {
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
		testGenerator.writeResults(folder.getRoot().toPath());

		assertThat(Files.exists(folder.getRoot().toPath().resolve("net/amygdalum/testrecorder/ScheduledTestGeneratorTestRecordedTest.java"))).isTrue();
	}

	@Test
	public void testWithDumpOnTimeInterval() throws Exception {
		testGenerator
			.withDumpMaximum(5)
			.withDumpTo(folder.getRoot().toPath())
			.withClassName("${counter}Test")
			.withDumpOnTimeInterval(1000);

		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).isEmpty();
		Thread.sleep(1000);
		testGenerator.accept(newSnapshot());
		testGenerator.await();

		assertThat(files()).containsExactlyInAnyOrder("2Test.java");

		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java");
		Thread.sleep(1000);
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java", "5Test.java");
	}

	@Test
	public void testWithDumpOnCounterInterval() throws Exception {
		testGenerator
			.withDumpMaximum(5)
			.withDumpTo(folder.getRoot().toPath())
			.withClassName("${counter}Test")
			.withDumpOnCounterInterval(2);

		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).isEmpty();
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java");
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java");
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java", "4Test.java");
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java", "4Test.java");
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).containsExactlyInAnyOrder("2Test.java", "4Test.java");
	}

	@Test
	public void testWithDumpOnShutDown() throws Exception {
		testGenerator
			.withDumpMaximum(5)
			.withDumpTo(folder.getRoot().toPath())
			.withClassName("${counter}Test")
			.withDumpOnShutDown(true);

		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.await();
		assertThat(files()).isEmpty();

		Thread shutdown = shutdownHooks().entrySet().stream()
			.filter(e -> e.getKey().getName().equals("$generate-shutdown"))
			.map(e -> e.getValue())
			.findFirst().orElseThrow(() -> new AssertionError("no shutdown thread"));

		shutdown.run();

		shutdown.join();

		assertThat(files()).containsExactlyInAnyOrder("5Test.java");
	}

	@Test
	public void testWithDumpOnShutDownConcurrent() throws Exception {
		testGenerator
			.withDumpMaximum(2)
			.withDumpTo(folder.getRoot().toPath())
			.withClassName("${counter}Test")
			.withDumpOnShutDown(true);

		ScheduledTestGenerator second = new ScheduledTestGenerator(config)
			.withDumpMaximum(2)
			.withDumpTo(folder.getRoot().toPath())
			.withClassName("${counter}SecondTest")
			.withDumpOnShutDown(true);

		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		testGenerator.accept(newSnapshot());
		second.accept(newSnapshot());
		second.accept(newSnapshot());
		second.accept(newSnapshot());
		assertThat(files()).isEmpty();

		Thread shutdown = shutdownHooks().entrySet().stream()
			.filter(e -> e.getKey().getName().equals("$generate-shutdown"))
			.map(e -> e.getValue())
			.findFirst().orElseThrow(() -> new AssertionError("no shutdown thread"));

		shutdown.run();
		shutdown.join();

		assertThat(files()).containsExactlyInAnyOrder("2Test.java", "2SecondTest.java");
	}

	private List<String> files() {
		try {
			Path path = folder.getRoot().toPath();
			return Files.walk(path)
				.filter(p -> Files.isRegularFile(p))
				.map(p -> p.getFileName().toString())
				.collect(toList());
		} catch (IOException e) {
			return emptyList();
		}
	}

	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new MethodSignature(declaringClass, new Annotation[0], resultType, methodName, new Annotation[0][0], argumentTypes));
	}

	private static int base = 8;

	private ContextSnapshot newSnapshot() {
		base++;

		ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, base + 4))));
		snapshot.setSetupArgs(literal(int.class, base + 8));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, base))));
		snapshot.setExpectArgs(literal(int.class, base + 8));
		snapshot.setExpectResult(literal(int.class, base + 14));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}

	private SerializedObject objectOf(Class<MyClass> type, SerializedField... fields) {
		SerializedObject setupThis = new SerializedObject(type);
		for (SerializedField field : fields) {
			setupThis.addField(field);
		}
		return setupThis;
	}

	private static Map<Thread, Thread> shutdownHooks() throws ClassNotFoundException {
		StaticShutdownHooks staticShutdownHooks = XRayInterface.xray(Class.forName("java.lang.ApplicationShutdownHooks"))
			.to(StaticShutdownHooks.class);

		return staticShutdownHooks.getHooks();
	}

	@SuppressWarnings("unused")
	private static class MyClass {

		private int field;

		public int intMethod(int arg) {
			return field + arg;
		}
	}

	interface StaticShutdownHooks {
		IdentityHashMap<Thread, Thread> getHooks();
	}

	interface OpenScheduledTestGenerator {
		void setDumpOnShutDown(Set<ScheduledTestGenerator> value);
	}
}
