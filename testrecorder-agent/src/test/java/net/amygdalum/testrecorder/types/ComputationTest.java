package net.amygdalum.testrecorder.types;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ComputationTest {

	@Test
	void testToString() throws Exception {
		assertThat(variable("str", String.class).toString())
			.isEqualTo("str:java.lang.String");
		assertThat(variable("str", null).toString())
			.isEqualTo("str:?");
		assertThat(variable("str", String.class, asList("String str = \"str\";", "str += \"str\";"))
			.toString()).isEqualTo(""
				+ "String str = \"str\";\n"
				+ "str += \"str\";\n"
				+ "str:java.lang.String");
	}

	@Test
	void testGetStatements() throws Exception {
		assertThat(variable("str", String.class).getStatements()).isEmpty();
		assertThat(variable("str", String.class, asList("String str = \"str\";", "str += \"str\";")).getStatements())
			.contains("String str = \"str\";", "str += \"str\";");
		assertThat(expression("(String) str", String.class).getStatements()).isEmpty();
		assertThat(expression("(String) str", String.class, asList("String str = \"str\";", "str += \"str\";")).getStatements())
			.contains("String str = \"str\";", "str += \"str\";");
	}

	@Test
	void testGetValue() throws Exception {
		assertThat(variable("str", String.class).getValue()).isEqualTo("str");
		assertThat(variable("str", String.class, asList("String str = \"str\";", "str += \"str\";")).getValue()).isEqualTo("str");
	}

	@Test
	void testGetType() throws Exception {
		assertThat(variable("str", String.class).getType()).isEqualTo(String.class);
		assertThat(variable("str", null).getType()).isNull();

	}

	@Test
	public void testIsStored() throws Exception {
		assertThat(variable("str", String.class).isStored()).isTrue();
		assertThat(expression("str", String.class).isStored()).isFalse();
	}

}
