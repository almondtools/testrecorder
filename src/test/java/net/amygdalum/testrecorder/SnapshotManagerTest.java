package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.SnapshotManager.DummyContextSnapshotTransaction.INVALID;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.xrayinterface.XRayInterface.xray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Type;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.SnapshotManager.ContextSnapshotTransaction;
import net.amygdalum.testrecorder.SnapshotManager.DummyContextSnapshotTransaction;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.util.CircularityLock;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Overridden;
import net.amygdalum.testrecorder.util.testobjects.Overriding;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SnapshotManagerTest {

	private SnapshotConsumer consumer;
	private AgentConfiguration config;
	private SnapshotManager snapshotManager;

	@BeforeEach
	public void before() throws Exception {
		consumer = Mockito.mock(SnapshotConsumer.class);
		config = defaultConfig()
			.withDefaultValue(SnapshotConsumer.class, () -> consumer);
		snapshotManager = new SnapshotManager(config);
		snapshotManager.registerRecordedMethod("getAttribute()Ljava/lang/String;", "net/amygdalum/testrecorder/util/testobjects/Bean", "getAttribute", "()Ljava/lang/String;");
		snapshotManager.registerRecordedMethod("setAttribute(Ljava/lang/String;)V", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
	}

	@Test
	public void testSetupVariablesMatching() throws Exception {
		Bean self = new Bean();
		self.setAttribute("tobeset");

		snapshotManager.setupVariables(self, "setAttribute(Ljava/lang/String;)V", "mystr");

		ContextSnapshot snapshot = snapshotManager.peek().get();

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

		Optional<ContextSnapshot> snapshot = snapshotManager.peek();
		assertThat(snapshot).isNotPresent();
	}

	@Test
	public void testSetupVariablesLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.setupVariables(new Bean(), "setAttribute(Ljava/lang/String;)V", "mystr");

		verify(snapshotManager, never()).push("setAttribute(Ljava/lang/String;)V");
		verify(snapshotManager, only()).setupVariables(any(), any(), any());
	}

	@Test
	public void testSetupVariablesExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("setupVariables")).when(snapshotManager).push(any());

		assertThatCode(() -> snapshotManager.setupVariables(new Bean(), "setAttribute(Ljava/lang/String;)V", "mystr"))
			.hasMessage("setupVariables");
		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testExpectVariablesMatching() throws Exception {
		snapshotManager.registerRecordedMethod("setAttribute(Ljava/lang/String;)V", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		Bean self = new Bean();
		snapshotManager.setupVariables(self, "setAttribute(Ljava/lang/String;)V", "mystr");
		ContextSnapshot snapshot = snapshotManager.peek().get();

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
		assertThat(snapshotManager.current()).isSameAs(INVALID);
	}

	@Test
	public void testExpectVariablesMismatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(L)V", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overriding self = new Overriding();

		ContextSnapshotTransaction pushed = snapshotManager.push("method(L)V");

		snapshotManager.expectVariables(self, "method(L)V", new Object[] { 1 });

		pushed.andConsume(snapshot -> {
			assertThat(snapshot.getExpectThis()).isNull();
			assertThat(snapshot.getExpectArgs()).isNull();
			assertThat(snapshot.getExpectGlobals()).isNull();
			assertThat(snapshot.getExpectOutput()).isEmpty();
		});
		assertThat(snapshotManager.current()).isNotSameAs(INVALID);
	}

	@Test
	public void testExpectVariablesLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.expectVariables(new Bean(), "setAttribute(Ljava/lang/String;)V", new Object[] { "mystr" });

		verify(snapshotManager, never()).pop("setAttribute(Ljava/lang/String;)V");
		verify(snapshotManager, only()).expectVariables(any(), any(), any());
	}

	@Test
	public void testExpectVariablesExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("expectVariables")).when(snapshotManager).pop(any());

		assertThatCode(() -> snapshotManager.expectVariables(new Bean(), "setAttribute(Ljava/lang/String;)V", new Object[] { "mystr" }))
			.hasMessage("expectVariables");
		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testExpectVariablesWithResultMatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overridden self = new Overridden();
		snapshotManager.setupVariables(self, "method(I)I", new Object[] { 1 });
		ContextSnapshot snapshot = snapshotManager.peek().get();

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
		assertThat(snapshotManager.current()).isSameAs(INVALID);
	}

	@Test
	public void testExpectVariablesWithResultMismatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overriding self = new Overriding();

		ContextSnapshotTransaction pushed = snapshotManager.push("method(L)V");

		snapshotManager.expectVariables(self, "method(I)I", 1, new Object[] { 1 });

		pushed.andConsume(snapshot -> {
			assertThat(snapshot.getExpectThis()).isNull();
			assertThat(snapshot.getExpectArgs()).isNull();
			assertThat(snapshot.getExpectGlobals()).isNull();
			assertThat(snapshot.getExpectOutput()).isEmpty();
		});
		assertThat(snapshotManager.current()).isNotSameAs(INVALID);
	}

	@Test
	public void testExpectVariablesWithResultLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.expectVariables(new Bean(), "getAttribute()Ljava/lang/String;", "myresult", new Object[0]);

		verify(snapshotManager, never()).pop("getAttribute()Ljava/lang/String;");
		verify(snapshotManager, only()).expectVariables(any(), any(), any(), any());
	}

	@Test
	public void testExpectVariablesWithResultExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("expectVariablesWithResult")).when(snapshotManager).pop(any());

		assertThatCode(() -> snapshotManager.expectVariables(new Bean(), "getAttribute()Ljava/lang/String;", "myresult", new Object[0]))
			.hasMessage("expectVariablesWithResult");
		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testThrowVariablesMatching() throws Exception {
		snapshotManager.registerRecordedMethod("method(I)I", "net/amygdalum/testrecorder/util/testobjects/Overridden", "method", "(I)I");
		Overridden self = new Overridden();
		snapshotManager.setupVariables(self, "method(I)I", new Object[] { 1 });
		ContextSnapshot snapshot = snapshotManager.peek().get();

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

		ContextSnapshotTransaction pushed = snapshotManager.push("method(I)I");

		snapshotManager.throwVariables(new RuntimeException("thrown by test"), self, "method(I)I", new Object[] { 1 });

		pushed.andConsume(snapshot -> {
			assertThat(snapshot.getExpectThis()).isNull();
			assertThat(snapshot.getExpectArgs()).isNull();
			assertThat(snapshot.getExpectResult()).isNull();
			assertThat(snapshot.getExpectException()).isNull();
			assertThat(snapshot.getExpectGlobals()).isNull();
			assertThat(snapshot.getExpectOutput()).isEmpty();
		});
		assertThat(snapshotManager.current()).isNotSameAs(INVALID);
	}

	@Test
	public void testThrowVariablesLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.throwVariables(new RuntimeException(), new Bean(), "setAttribute(Ljava/lang/String;)V", new Object[] { "mystr" });

		verify(snapshotManager, never()).pop("setAttribute(Ljava/lang/String;)V");
		verify(snapshotManager, only()).throwVariables(any(), any(), any(), any());
	}

	@Test
	public void testThrowVariablesExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("throwVariables")).when(snapshotManager).pop(any());

		assertThatCode(() -> snapshotManager.throwVariables(new RuntimeException(), new Bean(), "setAttribute(Ljava/lang/String;)V", new Object[] { "mystr" }))
			.hasMessage("throwVariables");
		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testPushPop() throws Exception {
		snapshotManager.registerRecordedMethod("signature", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");

		snapshotManager.push("signature");

		Optional<ContextSnapshot> snapshot = snapshotManager.peek();

		assertThat(snapshot).isPresent();

		snapshotManager.pop("signature");

		snapshot = snapshotManager.peek();

		assertThat(snapshot).isNotPresent();
	}

	@Test
	public void testNoPushPop() throws Exception {
		snapshotManager.registerRecordedMethod("signature", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");

		ContextSnapshotTransaction ta = snapshotManager.pop("signature");

		Optional<ContextSnapshot> snapshot = snapshotManager.peek();

		assertThat(ta).isSameAs(DummyContextSnapshotTransaction.INVALID);
		assertThat(snapshot).isNotPresent();

	}

	@Test
	public void testPushPopInvalidating() throws Exception {
		snapshotManager.registerRecordedMethod("signature1", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.registerRecordedMethod("signature2", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.registerRecordedMethod("signature3", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.push("signature1");
		ContextSnapshotTransaction snapshot2 = snapshotManager.push("signature2");
		ContextSnapshotTransaction snapshot3 = snapshotManager.push("signature3");

		snapshotManager.pop("signature1").andConsume(snapshot -> {
			assertThat(snapshot).isNotNull();
			assertThat(snapshot.isValid()).isTrue();
		});
		;

		snapshot2.andConsume(snapshot -> assertThat(snapshot.isValid()).isFalse());
		snapshot3.andConsume(snapshot -> assertThat(snapshot.isValid()).isFalse());
	}

	@Test
	public void testCurrentAndPeek() throws Exception {
		snapshotManager.registerRecordedMethod("signature1", "net/amygdalum/testrecorder/util/testobjects/Bean", "setAttribute", "(Ljava/lang/String;)V");
		snapshotManager.registerRecordedMethod("signature2", "net/amygdalum/testrecorder/util/testobjects/Bean", "getAttribute", "()Ljava/lang/String;");

		snapshotManager.push("signature1");
		snapshotManager.push("signature2");

		assertThat(snapshotManager.all()).hasSize(2);
		assertThat(snapshotManager.peek())
			.isPresent()
			.satisfies(snapshot -> assertThat(snapshot.get().getMethodName()).isEqualTo("getAttribute"));
	}

	@Test
	public void testInputVariablesLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.inputVariables(new Bean(), "getAttribute", String.class, new Class[0]);

		verify(snapshotManager, never()).all();
		verify(snapshotManager, only()).inputVariables(any(), any(), any(), any());
	}

	@Test
	public void testInputVariablesExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("input variables")).when(snapshotManager).all();

		assertThatCode(() -> snapshotManager.inputVariables(new Bean(), "getAttribute", String.class, new Class[0]))
			.hasMessage("input variables");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testInputArgumentsLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.inputArguments(42);

		verify(snapshotManager, never()).current();
		verify(snapshotManager, only()).inputArguments(anyInt(), any());
	}

	@Test
	public void testInputArgumentsExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("input arguments")).when(snapshotManager).current();

		assertThatCode(() -> snapshotManager.inputArguments(42))
			.hasMessage("input arguments");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testInputResultLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.inputResult(42, "mystr");

		verify(snapshotManager, never()).current();
		verify(snapshotManager, only()).inputResult(anyInt(), any());
	}

	@Test
	public void testInputResultExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("input result")).when(snapshotManager).current();

		assertThatCode(() -> snapshotManager.inputResult(42, "mystr"))
			.hasMessage("input result");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testInputVoidResultLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.inputVoidResult(42);

		verify(snapshotManager, never()).current();
		verify(snapshotManager, only()).inputVoidResult(anyInt());
	}

	@Test
	public void testInputVoidResultExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("input void result")).when(snapshotManager).current();

		assertThatCode(() -> snapshotManager.inputVoidResult(42))
			.hasMessage("input void result");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testOutputVariablesLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.outputVariables(new Bean(), "setAttribute", void.class, new Type[] { String.class });

		verify(snapshotManager, never()).all();
	}

	@Test
	public void testOutputVaraiblesExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("output variables")).when(snapshotManager).all();

		assertThatCode(() -> snapshotManager.outputVariables(new Bean(), "setAttribute", void.class, new Type[] { String.class }))
			.hasMessage("output variables");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testOutputArgumentsLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.outputArguments(42);

		verify(snapshotManager, never()).current();
		verify(snapshotManager, only()).outputArguments(anyInt(), any());
	}

	@Test
	public void testOutputArgumentsExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("output arguments")).when(snapshotManager).current();

		assertThatCode(() -> snapshotManager.outputArguments(42))
			.hasMessage("output arguments");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testOutputResultLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.outputResult(42, "mystr");

		verify(snapshotManager, never()).current();
		verify(snapshotManager, only()).outputResult(anyInt(), any());
	}

	@Test
	public void testOutputResultExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("output result")).when(snapshotManager).current();

		assertThatCode(() -> snapshotManager.outputResult(42, "mystr"))
			.hasMessage("output result");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	@Test
	public void testOutputVoidResultLocking() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		xray(snapshotManager).to(OpenSnapshotManager.class).getLock().acquire();

		snapshotManager.outputVoidResult(42);

		verify(snapshotManager, never()).current();
		verify(snapshotManager, only()).outputVoidResult(anyInt());
	}

	@Test
	public void testOutputVoidResultExceptionReleasesLock() throws Exception {
		SnapshotManager snapshotManager = spy(this.snapshotManager);
		doThrow(new RuntimeException("output void result")).when(snapshotManager).current();

		assertThatCode(() -> snapshotManager.outputVoidResult(42))
			.hasMessage("output void result");

		boolean locked = xray(snapshotManager).to(OpenSnapshotManager.class).getLock().locked();
		assertThat(locked).isFalse();
	}

	interface OpenSnapshotManager {
		CircularityLock getLock();
	}

}
