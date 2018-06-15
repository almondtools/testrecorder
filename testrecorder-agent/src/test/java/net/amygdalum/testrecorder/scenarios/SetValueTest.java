package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class SetValueTest {

	@Test
	public void testHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Set<String> m = new HashSet<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(m)).contains(
			"HashSet hashSet1 = new HashSet<>();",
			"hashSet1.add(\"foo\");",
			"hashSet1.add(\"bar\");");
	}

	@Test
	public void testResultType() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Set<String> m = new LinkedHashSet<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(parameterized(Set.class, null, String.class), m)).containsWildcardPattern(""
			+ "Set<String> linkedHashSet1 = new LinkedHashSet<>();*"
			+ "linkedHashSet1.add(\"foo\");*"
			+ "linkedHashSet1.add(\"bar\");*");
	}

}
