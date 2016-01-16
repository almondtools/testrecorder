package com.almondtools.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static com.almondtools.testrecorder.visitors.TypeManager.parameterized;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.almondtools.testrecorder.CodeSerializer;

public class ListValueTest {

	@Test
	public void testArrayList() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		List<String> m = new ArrayList<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(m), containsPattern(""
			+ "ArrayList list1 = new ArrayList<>();*"
			+ "list1.add(\"foo\");*"
			+ "list1.add(\"bar\");"));
	}

	@Test
	public void testResultType() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		List<String> m = new ArrayList<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(parameterized(List.class, null, String.class), m), containsPattern(""
			+ "List<String> list1 = new ArrayList<>();*"
			+ "list1.add(\"foo\");*"
			+ "list1.add(\"bar\");"));
	}

}
