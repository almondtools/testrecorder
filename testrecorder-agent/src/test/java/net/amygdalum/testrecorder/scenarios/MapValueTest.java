package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class MapValueTest {

	@Test
	public void testHashMap() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("bar", new Integer(21));

		assertThat(codeSerializer.serialize(m)).containsWildcardPattern(""
			+ "HashMap hashMap1 = new HashMap<>();*"
			+ "hashMap1.put(\"bar\", 21);");
	}

	@Test
	public void testResultType() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("bar", new Integer(21));

		assertThat(codeSerializer.serialize(parameterized(Map.class, null, String.class, Integer.class), m)).containsWildcardPattern(""
			+ "LinkedHashMap temp1 = new LinkedHashMap<>();*"
			+ "temp1.put(\"bar\", 21);*"
			+ "Map<String, Integer> map1 = temp1;");
	}

}
