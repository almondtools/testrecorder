package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.assertjconventions.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.CodeSerializer;

public class SetValueTest {

	@Test
	public void testHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Set<String> m = new HashSet<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(m)).contains(
			"HashSet set1 = new HashSet<>();",
			"set1.add(\"foo\");",
			"set1.add(\"bar\");");
	}

	@Test
	public void testResultType() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Set<String> m = new LinkedHashSet<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(parameterized(Set.class, null, String.class), m)).containsWildcardPattern(""
			+ "LinkedHashSet temp1 = new LinkedHashSet<>();*"
			+ "temp1.add(\"foo\");*"
			+ "temp1.add(\"bar\");*"
			+ "Set<String> set1 = temp1;");
	}

}
