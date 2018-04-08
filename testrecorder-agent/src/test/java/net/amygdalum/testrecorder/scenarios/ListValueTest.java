package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class ListValueTest {

	@Test
	public void testArrayList() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		List<String> m = new ArrayList<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(m)).containsWildcardPattern(""
			+ "ArrayList arrayList1 = new ArrayList<>();*"
			+ "arrayList1.add(\"foo\");*"
			+ "arrayList1.add(\"bar\");");
	}

	@Test
	public void testResultType() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		List<String> m = new ArrayList<String>();
		m.add("foo");
		m.add("bar");

		assertThat(codeSerializer.serialize(parameterized(List.class, null, String.class), m)).containsWildcardPattern(""
			+ "ArrayList temp1 = new ArrayList<>();*"
			+ "temp1.add(\"foo\");*"
			+ "temp1.add(\"bar\");*"
			+ "List<String> list1 = temp1;");
	}

}
