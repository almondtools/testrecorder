package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ContextSnapshotTest {

	@Test
	public void testMethodSnapshot() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		assertThat(snapshot.isValid(), is(true));
		assertThat(snapshot.getDeclaringClass(), equalTo(ArrayList.class));
		assertThat(snapshot.getResultType(), equalTo(boolean.class));
		assertThat(snapshot.getMethodName(), equalTo("add"));
		assertThat(snapshot.getArgumentTypes(), hasItemInArray(Object.class));
	}

	@Test
	public void testInvalidate() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.invalidate();

		assertThat(snapshot.isValid(), is(false));
	}

	@Test
	public void testGetThisType() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class).withResult(List.class);
		setupThis.add(literal(String.class, "setup"));

		snapshot.setSetupThis(setupThis);

		assertThat(snapshot.getThisType(), equalTo(ArrayList.class));
	}

	@Test
	public void testSetGetSetupThis() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class).withResult(List.class);
		setupThis.add(literal(String.class, "setup"));

		snapshot.setSetupThis(setupThis);

		assertThat(snapshot.getSetupThis(), equalTo(setupThis));
	}

	@Test
	public void testSetGetExpectThis() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList expectedThis = new SerializedList(ArrayList.class).withResult(List.class);
		expectedThis.add(literal(String.class, "expected"));

		snapshot.setExpectThis(expectedThis);

		assertThat(snapshot.getExpectThis(), equalTo(expectedThis));
	}

	@Test
	public void testSetGetSetupArgs() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setSetupArgs(literal(String.class, "a"), literal(String.class, "b"));

		assertThat(snapshot.getSetupArgs(), arrayContaining(literal(String.class, "a"), literal(String.class, "b")));
	}

	@Test
	public void testSetGetExpectArgs() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectArgs(literal(String.class, "c"), literal(String.class, "d"));

		assertThat(snapshot.getExpectArgs(), arrayContaining(literal(String.class, "c"), literal(String.class, "d")));
	}

	@Test
	public void testSetGetExpectResult() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectResult(literal(boolean.class, true));

		assertThat(snapshot.getExpectResult(), equalTo(literal(boolean.class, true)));
	}

	@Test
	public void testSetGetExpectException() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedObject expectException = new SerializedObject(NullPointerException.class);

		snapshot.setExpectException(expectException);

		assertThat(snapshot.getExpectException(), sameInstance(expectException));
	}

}
