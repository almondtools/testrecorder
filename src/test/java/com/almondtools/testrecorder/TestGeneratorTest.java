package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.almondtools.testrecorder.values.SerializedField;
import com.almondtools.testrecorder.values.SerializedObject;
import com.almondtools.testrecorder.visitors.Computation;
import com.almondtools.testrecorder.visitors.ImportManager;
import com.almondtools.testrecorder.visitors.LocalVariableNameGenerator;
import com.almondtools.testrecorder.visitors.SerializedValueVisitorFactory;
import com.almondtools.testrecorder.visitors.TestComputationValueVisitor;

public class TestGeneratorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private TestGenerator testGenerator;

	@Before
	public void before() throws Exception {
		testGenerator = new TestGenerator();
	}

	@Test
	public void testAccept() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));

		testGenerator.accept(snapshot);

		assertThat(testGenerator.testsFor(MyClass.class), contains(allOf(
			containsString("int field = 12;"),
			containsString("intMethod(16);"),
			containsString("equalTo(22)"),
			containsString("int field = 8;"))));
	}

	@Test
	public void testSetSetup() throws Exception {
		testGenerator.setSetup(new SerializedValueVisitorFactory() {

			@Override
			public SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, ImportManager imports) {
				return new TestComputationValueVisitor();
			}
		});
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));

		testGenerator.accept(snapshot);

		assertThat(testGenerator.testsFor(MyClass.class), contains(allOf(
			containsString("(com.almondtools.testrecorder.TestGeneratorTest$MyClass/"),
			containsString("int field: 12"),
			containsString("intMethod((16))"),
			containsString("equalTo(22)"),
			containsString("int field = 8;"))));
	}

	@Test
	public void testSetMatcher() throws Exception {
		testGenerator.setMatcher(new SerializedValueVisitorFactory() {

			@Override
			public SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, ImportManager imports) {
				return new TestComputationValueVisitor();
			}
		});
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));

		testGenerator.accept(snapshot);

		assertThat(testGenerator.testsFor(MyClass.class), contains(allOf(
			containsString("int field = 12;"),
			containsString("intMethod(16);"),
			containsString("(22)"),
			containsString("(com.almondtools.testrecorder.TestGeneratorTest$MyClass/"),
			containsString("int field: 8"))));
	}

	@Test
	public void testTestsForEmpty() throws Exception {
		assertThat(testGenerator.testsFor(MyClass.class), empty());
	}

	@Test
	public void testTestsForAfterClear() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));
		testGenerator.accept(snapshot);

		testGenerator.clearResults();

		assertThat(testGenerator.testsFor(MyClass.class), empty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRenderCode() throws Exception {
		ContextSnapshot snapshot1 = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot1.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 12))));
		snapshot1.setSetupArgs(literal(int.class, 16));
		snapshot1.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 8))));
		snapshot1.setExpectArgs(literal(int.class, 16));
		snapshot1.setExpectResult(literal(int.class, 22));
		ContextSnapshot snapshot2 = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot2.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 13))));
		snapshot2.setSetupArgs(literal(int.class, 17));
		snapshot2.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 9))));
		snapshot2.setExpectArgs(literal(int.class, 17));
		snapshot2.setExpectResult(literal(int.class, 23));

		testGenerator.accept(snapshot1);
		testGenerator.accept(snapshot2);

		assertThat(testGenerator.renderTest(MyClass.class), allOf(
			containsString("int field = 12;"),
			containsString("intMethod(16);"),
			containsString("equalTo(22)"),
			containsString("int field = 8;"),
			containsString("int field = 13;"),
			containsString("intMethod(17);"),
			containsString("equalTo(23)"),
			containsString("int field = 9;")));
	}

	@Test
	public void testComputeClassName() throws Exception {
		assertThat(testGenerator.computeClassName(MyClass.class), equalTo("MyClassRecordedTest"));
	}

	@Test
	public void testFromRecorded() throws Exception {
		assertThat(TestGenerator.fromRecorded(new MyClass()), nullValue());
		assertThat(TestGenerator.fromRecorded(null), nullValue());
	}

	@Test
	public void testWriteResults() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(MyClass.class, int.class, "intMethod", int.class);
		snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 12))));
		snapshot.setSetupArgs(literal(int.class, 16));
		snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField("field", int.class, literal(int.class, 8))));
		snapshot.setExpectArgs(literal(int.class, 16));
		snapshot.setExpectResult(literal(int.class, 22));

		testGenerator.accept(snapshot);

		testGenerator.writeResults(folder.getRoot().toPath());

		assertThat(Files.exists(folder.getRoot().toPath().resolve("com/almondtools/testrecorder/MyClassRecordedTest.java")), is(true));
	}

	private SerializedObject objectOf(Class<MyClass> type, SerializedField... fields) {
		SerializedObject setupThis = new SerializedObject(type);
		setupThis.setObjectType(type);
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
