package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Overridden;
import net.amygdalum.testrecorder.util.testobjects.Overriding;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SnapshotManagerTest {

	private SnapshotConsumer consumer;
	private SnapshotManager snapshotManager;

	@BeforeEach
	public void before() throws Exception {
		consumer = Mockito.mock(SnapshotConsumer.class);
		snapshotManager = new SnapshotManager(new TestTestRecorderAgentConfig(consumer));
	}

	@Test
	public void testSetupVariablesMatching() throws Exception {
		snapshotManager.registerRecordedMethod("setAttribute(Ljava/lang/String;)V", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		Bean self = new Bean();
		self.setAttribute("tobeset");

		snapshotManager.setupVariables(self, "setAttribute(Ljava/lang/String;)V", "mystr");

		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		assertThat(snapshot.getSetupThis())
			.isInstanceOf(SerializedObject.class)
			.isEqualToIgnoringGivenFields(new SerializedObject(Bean.class)
				.withFields(new SerializedField(Bean.class, "attribute", String.class, literal("tobeset"))), "id");
		assertThat(snapshot.getSetupArgs())
			.hasSize(1)
			.contains(literal("mystr"));
		assertThat(snapshot.getSetupGlobals()).isEmpty();
		assertThat(snapshot.getSetupInput()).isEmpty();
	}

	@Test
	public void testSetupVariablesMismatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overriding self = new Overriding();

		snapshotManager.setupVariables(self, "method(I)I", 1);

		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		assertThat(snapshot.getSetupThis()).isNull();
		assertThat(snapshot.getSetupArgs()).isNull();
		assertThat(snapshot.getSetupGlobals()).isNull();
		assertThat(snapshot.getSetupInput()).isEmpty();
	}

	@Test
	public void testExpectVariablesMatching() throws Exception {
		snapshotManager.registerRecordedMethod("setAttribute(Ljava/lang/String;)V", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		Bean self = new Bean();
		snapshotManager.setupVariables(self, "setAttribute(Ljava/lang/String;)V", "mystr");
		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		self.setAttribute("hasbeenset");
		snapshotManager.expectVariables(self, "setAttribute(Ljava/lang/String;)V", new Object[] { "mystr" });

		assertThat(snapshot.getExpectThis())
			.isInstanceOf(SerializedObject.class)
			.isEqualToIgnoringGivenFields(new SerializedObject(Bean.class)
				.withFields(new SerializedField(Bean.class, "attribute", String.class, literal("hasbeenset"))), "id");
		assertThat(snapshot.getExpectArgs())
			.hasSize(1)
			.contains(literal("mystr"));
		assertThat(snapshot.getExpectResult()).isNull();
		assertThat(snapshot.getExpectException()).isNull();
		assertThat(snapshot.getExpectGlobals()).isEmpty();
		assertThat(snapshot.getExpectOutput()).isEmpty();
	}

	@Test
	public void testExpectVariablesWithResultMatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overridden self = new Overridden();
		snapshotManager.setupVariables(self, "method(I)I", new Object[] { 1 });
		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		snapshotManager.expectVariables(self, "method(I)I", 2, new Object[] { 1 });

		assertThat(snapshot.getExpectThis())
			.isInstanceOf(SerializedObject.class)
			.isEqualToIgnoringGivenFields(new SerializedObject(Overridden.class), "id");
		assertThat(snapshot.getExpectArgs())
			.hasSize(1)
			.contains(literal(int.class, 1));
		assertThat(snapshot.getExpectResult()).isEqualTo(literal(int.class, 2));
		assertThat(snapshot.getExpectException()).isNull();
		assertThat(snapshot.getExpectGlobals()).isEmpty();
		assertThat(snapshot.getExpectOutput()).isEmpty();
	}

	@Test
	public void testExpectVariablesWithMismatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(L)V", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overriding self = new Overriding();
		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		snapshotManager.expectVariables(self, "method(L)V", new Object[] { 1 });

		assertThat(snapshot.getExpectThis()).isNull();
		assertThat(snapshot.getExpectArgs()).isNull();
		assertThat(snapshot.getExpectGlobals()).isNull();
		assertThat(snapshot.getExpectOutput()).isEmpty();
	}

	@Test
	public void testExpectVariablesWithResultMismatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overriding self = new Overriding();
		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		snapshotManager.expectVariables(self, "method(I)I", 1, new Object[] { 1 });

		assertThat(snapshot.getExpectThis()).isNull();
		assertThat(snapshot.getExpectArgs()).isNull();
		assertThat(snapshot.getExpectGlobals()).isNull();
		assertThat(snapshot.getExpectOutput()).isEmpty();
	}

	@Test
	public void testThrowVariablesMatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overridden self = new Overridden();
		snapshotManager.setupVariables(self, "method(I)I", new Object[] { 1 });
		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		snapshotManager.throwVariables(new RuntimeException("thrown by test"), self, "method(I)I", new Object[] { 1 });

		assertThat(snapshot.getExpectThis())
			.isInstanceOf(SerializedObject.class)
			.isEqualToIgnoringGivenFields(new SerializedObject(Overridden.class), "id");
		assertThat(snapshot.getExpectArgs())
			.hasSize(1)
			.contains(literal(int.class, 1));
		assertThat(snapshot.getExpectResult()).isNull();
		assertThat(snapshot.getExpectException())
			.isNotNull()
			.isInstanceOf(SerializedObject.class)
			.satisfies(exception -> {
				assertThat(((SerializedObject) exception).getField("detailMessage").get().getValue())
					.isEqualToIgnoringGivenFields(literal("thrown by test"), "id");
			});
		assertThat(snapshot.getExpectGlobals()).isEmpty();
		assertThat(snapshot.getExpectOutput()).isEmpty();
	}

	@Test
	public void testThrowVariablesMismatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overriding self = new Overriding();
		ContextSnapshot snapshot = snapshotManager.current().getSnapshot();

		snapshotManager.throwVariables(new RuntimeException("thrown by test"), self, "method(I)I", new Object[] { 1 });

		assertThat(snapshot.getExpectThis()).isNull();
		;
		assertThat(snapshot.getExpectArgs()).isNull();
		assertThat(snapshot.getExpectResult()).isNull();
		assertThat(snapshot.getExpectException()).isNull();
		assertThat(snapshot.getExpectGlobals()).isNull();
		assertThat(snapshot.getExpectOutput()).isEmpty();
	}

	@Test
	public void testPushPop() throws Exception {
		snapshotManager.registerRecordedMethod("signature", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.push("signature");
		SnapshotProcess process = snapshotManager.pop("signature");
		assertThat(process.getSnapshot()).isNotNull();
	}

	@Test
	public void testNoPushPop() throws Exception {
		snapshotManager.registerRecordedMethod("signature", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		assertThatThrownBy(() -> snapshotManager.pop("signature"))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testPushPopInvalidating() throws Exception {
		snapshotManager.registerRecordedMethod("signature1", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.registerRecordedMethod("signature2", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.registerRecordedMethod("signature3", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.push("signature1");
		snapshotManager.push("signature2");
		SnapshotProcess process2 = snapshotManager.current();
		snapshotManager.push("signature3");
		SnapshotProcess process3 = snapshotManager.current();

		SnapshotProcess process = snapshotManager.pop("signature1");

		assertThat(process.getSnapshot()).isNotNull();
		assertThat(process.getSnapshot().isValid()).isTrue();
		assertThat(process2.getSnapshot().isValid()).isFalse();
		assertThat(process3.getSnapshot().isValid()).isFalse();
	}

}
