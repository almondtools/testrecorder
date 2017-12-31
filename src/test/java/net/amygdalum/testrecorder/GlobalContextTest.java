package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

import com.almondtools.conmatch.exceptions.ExceptionMatcher;

import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.util.testobjects.Static;

public class GlobalContextTest {

	@Test
	public void testGlobals() throws Exception {
		GlobalContext globalContext = new GlobalContext();
		globalContext.add("net.amygdalum.testrecorder.util.testobjects.Static", "global");
		assertThat(globalContext.globals()).containsExactly(Types.getDeclaredField(Static.class, "global"));

		globalContext.add("net.amygdalum.testrecorder.util.testobjects.Static", "CONSTANT");
		assertThat(globalContext.globals()).containsExactly(Types.getDeclaredField(Static.class, "global"));
	}

	@Test
	public void testGlobalsFinishesAddPhase() throws Exception {
		GlobalContext globalContext = new GlobalContext();
		globalContext.add("net.amygdalum.testrecorder.util.testobjects.Static", "global");
		globalContext.globals();
		globalContext.add("net.amygdalum.testrecorder.util.testobjects.Static", "CONSTANT");

		assertThat(globalContext.globals()).containsExactly(Types.getDeclaredField(Static.class, "global"));
	}

	@Test
	public void testGlobalsOnNonexistingField() throws Exception {
		GlobalContext globalContext = new GlobalContext();
		globalContext.add("net.amygdalum.testrecorder.util.testobjects.Static", "notexisting");
		assertThat(Throwables.capture(() -> globalContext.globals()), ExceptionMatcher.matchesException(SerializationException.class).withCause(NoSuchFieldException.class));
	}
}
