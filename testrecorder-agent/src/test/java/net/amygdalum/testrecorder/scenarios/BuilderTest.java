package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class BuilderTest {

	@Test
	public void testCodeSerializerSimpleBuilder() throws Exception {
		CodeSerializer codeSerializer = builderSerializer();

		assertThat(codeSerializer.serialize(new Buildable.Builder()
			.withA(22)
			.withB("B")
			.build()))
				.contains(""
					+ "new Builder()"
					+ ".withA(22)"
					+ ".withB(\"B\")"
					+ ".build()");
	}

	@Test
	public void testCodeSerializerDependentBuilder() throws Exception {
		CodeSerializer codeSerializer = builderSerializer();

		assertThat(codeSerializer.serialize(new BuildableWithDependencies.Builder()
			.withA(asList(1, 2))
			.withB(new Buildable.Builder()
				.withA(22)
				.withB("B")
				.build())
			.build()))
				.contains(""
					+ "new Builder()"
					+ ".withA(list1)"
					+ ".withB(buildable1)"
					+ ".build()")
				.contains("list1 =")
				.contains("buildable1 =");
	}

	private static CodeSerializer builderSerializer() {
		CodeSerializer codeSerializer = new CodeSerializer("net.amygdalum.testrecorder.scenarios");
		codeSerializer.getTypes().registerTypes(Buildable.class);
		codeSerializer.getTypes().registerTypes(BuildableWithDependencies.class);
		return codeSerializer;
	}

}