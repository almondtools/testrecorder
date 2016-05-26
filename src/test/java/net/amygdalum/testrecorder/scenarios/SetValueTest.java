package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import net.amygdalum.testrecorder.CodeSerializer;

public class SetValueTest {

	@Test
	public void testHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Set<String> m = new HashSet<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(m), allOf(
			containsString("HashSet set1 = new HashSet<>();"),
			containsString("set1.add(\"foo\");"),
			containsString("set1.add(\"bar\");")));
	}

	@Test
	public void testResultType() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		Set<String> m = new LinkedHashSet<String>();
		m.add("foo");
		m.add("bar");
		
		assertThat(codeSerializer.serialize(parameterized(Set.class, null, String.class), m), containsPattern(""
			+ "LinkedHashSet temp1 = new LinkedHashSet<>();*"
			+ "temp1.add(\"foo\");*"
			+ "temp1.add(\"bar\");*"
			+ "Set<String> set1 = temp1;"));
	}
	 
}
