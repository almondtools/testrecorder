package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TestComputationValueVisitor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class TestGeneratorTest {

	private static SnapshotManager saveManager;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private TestGenerator testGenerator;

	@BeforeClass
	public static void beforeClass() throws Exception {
		saveManager = SnapshotManager.MANAGER;
	}

	@AfterClass
	public static void afterClass() throws Exception {
		SnapshotManager.MANAGER = saveManager;
	}

	@Before
	public void before() throws Exception {
		testGenerator = new TestGenerator(null);
	}

	@Test
	public void testAccept() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.andThen(() -> {
			assertThat(testGenerator.testsFor(MyClass.class), contains(allOf(
				containsString("int field = 12;"),
				containsString("intMethod(16);"),
				containsString("equalTo(22)"),
				containsString("int field = 8;"))));
		});
	}

	@Test
	public void testSetSetup() throws Exception {
		testGenerator.setSetup(new DeserializerFactory() {

			@Override
			public Deserializer<Computation> create(LocalVariableNameGenerator locals, TypeManager types) {
				return new TestComputationValueVisitor();
			}

			@Override
			public Type resultType(Type value) {
				return value;
			}
		});
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.andThen(() -> {
			assertThat(testGenerator.testsFor(MyClass.class), contains(allOf(
				containsString("(net.amygdalum.testrecorder.TestGeneratorTest$MyClass/"),
				containsString("int field: 12"),
				containsString("intMethod((16))"),
				containsString("equalTo(22)"),
				containsString("int field = 8;"))));
		});
	}

	@Test
	public void testSetMatcher() throws Exception {
		testGenerator.setMatcher(new DeserializerFactory() {

			@Override
			public Deserializer<Computation> create(LocalVariableNameGenerator locals, TypeManager types) {
				return new TestComputationValueVisitor();
			}

			@Override
			public Type resultType(Type value) {
				return value;
			}
		});
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.andThen(() -> {
			assertThat(testGenerator.testsFor(MyClass.class), contains(allOf(
				containsString("int field = 12;"),
				containsString("intMethod(16);"),
				containsString("(22)"),
				containsString("(net.amygdalum.testrecorder.TestGeneratorTest$MyClass/"),
				containsString("int field: 8"))));
		});
	}

	@Test
	public void testTestsForEmpty() throws Exception {
		assertThat(testGenerator.testsFor(MyClass.class), empty());
	}

	@Test
	public void testTestsForAfterClear() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);
		testGenerator.accept(snapshot);

		testGenerator.clearResults();

		assertThat(testGenerator.testsFor(MyClass.class), empty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRenderCode() throws Exception {
		ContextSnapshot snapshot1 = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot1.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot1.setSetupArgs(literal(int.class, 16));
		snapshot1.setSetupGlobals(new SerializedField[0]);
		snapshot1.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot1.setExpectArgs(literal(int.class, 16));
		snapshot1.setExpectResult(literal(int.class, 22));
		snapshot1.setExpectGlobals(new SerializedField[0]);
		ContextSnapshot snapshot2 = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot2.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 13))));
		snapshot2.setSetupArgs(literal(int.class, 17));
		snapshot2.setSetupGlobals(new SerializedField[0]);
		snapshot2.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 9))));
		snapshot2.setExpectArgs(literal(int.class, 17));
		snapshot2.setExpectResult(literal(int.class, 23));
		snapshot2.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot1);
		testGenerator.accept(snapshot2);

		testGenerator.andThen(() -> {
			assertThat(testGenerator.renderTest(MyClass.class), allOf(
				containsString("int field = 12;"),
				containsString("intMethod(16);"),
				containsString("equalTo(22)"),
				containsString("int field = 8;"),
				containsString("int field = 13;"),
				containsString("intMethod(17);"),
				containsString("equalTo(23)"),
				containsString("int field = 9;")));
		});
	}

	@Test
	public void testComputeClassName() throws Exception {
		assertThat(testGenerator.computeClassName(MyClass.class), equalTo("MyClassRecordedTest"));
	}

	@Test
	public void testFromRecordedIfConsumerIsNull() throws Exception {
		SnapshotManager.MANAGER = new SnapshotManager(new DefaultTestRecorderAgentConfig() {
			@Override
			public SnapshotConsumer getSnapshotConsumer() {
				return null;
			}
		});
		assertThat(TestGenerator.fromRecorded(), nullValue());
		assertThat(TestGenerator.fromRecorded(), nullValue());
	}

	@Test
	public void testFromRecordedIfConsumerIsNonNull() throws Exception {
		TestGenerator tg = new TestGenerator(null);
		SnapshotManager.MANAGER = new SnapshotManager(new DefaultTestRecorderAgentConfig() {
			@Override
			public SnapshotConsumer getSnapshotConsumer() {
				return tg;
			}
		});
		assertThat(TestGenerator.fromRecorded(), sameInstance(tg));
		assertThat(TestGenerator.fromRecorded(), sameInstance(tg));
	}

	@Test
	public void testFromRecordedIfConsumerIsNotTestGenerator() throws Exception {
		SnapshotManager.MANAGER = new SnapshotManager(new DefaultTestRecorderAgentConfig() {
			@Override
			public SnapshotConsumer getSnapshotConsumer() {
				return new SnapshotConsumer() {

					@Override
					public void accept(ContextSnapshot snapshot) {
					}

					@Override
					public void close() {
					}
				};
			}
		});
		assertThat(TestGenerator.fromRecorded(), nullValue());
	}

	@Test
	public void testWriteResults() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(MyClass.class, "field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		snapshot.setExpectGlobals(new SerializedField[0]);

		testGenerator.accept(snapshot);

		testGenerator.andThen(() -> {
			testGenerator.writeResults(folder.getRoot().toPath());

			assertThat(Files.exists(folder.getRoot().toPath().resolve("net/amygdalum/testrecorder/MyClassRecordedTest.java")), is(true));
		});
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
