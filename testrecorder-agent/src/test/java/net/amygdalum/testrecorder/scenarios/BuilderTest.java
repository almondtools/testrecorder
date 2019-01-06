package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.generator.JUnit4TestTemplate;
import net.amygdalum.testrecorder.generator.TestGeneratorProfile;
import net.amygdalum.testrecorder.generator.TestTemplate;
import net.amygdalum.testrecorder.hints.Builder;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

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

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	public void testCodeSerializerWithExternalHints(ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.scenarios.BuilderTest$ExternalAnnotationGeneratorProfile".getBytes());

		CodeSerializer codeSerializer = builderSerializer();

		assertThat(codeSerializer.serialize(new BuildableWithoutAnnotation.Builder()
			.withA(22)
			.withB("B")
			.build()))
				.contains(""
					+ "new Builder()"
					+ ".withA(22)"
					+ ".withB(\"B\")"
					+ ".build()");
	}

	private static CodeSerializer builderSerializer() {
		CodeSerializer codeSerializer = new CodeSerializer("net.amygdalum.testrecorder.scenarios");
		codeSerializer.getTypes().registerTypes(Buildable.class);
		codeSerializer.getTypes().registerTypes(BuildableWithDependencies.class);
		codeSerializer.getTypes().registerTypes(BuildableWithoutAnnotation.class);
		return codeSerializer;
	}

	public static class ExternalAnnotationGeneratorProfile implements TestGeneratorProfile {

		@Override
		public List<CustomAnnotation> annotations() {
			return asList(new CustomAnnotation(BuildableWithoutAnnotation.class, new Builder() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return Builder.class;
				}

				@Override
				public Class<?> builder() {
					return BuildableWithoutAnnotation.Builder.class;
				}
			}));
		}

		@Override
		public Class<? extends TestTemplate> template() {
			return JUnit4TestTemplate.class;
		}

	}

}