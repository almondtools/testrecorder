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
import net.amygdalum.testrecorder.hints.Factory;
import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

public class FactoryTest {

	@Test
	public void testCodeSerializerSimpleBuilder() throws Exception {
		CodeSerializer codeSerializer = factorySerializer();

		assertThat(codeSerializer.serialize(Factorable.create("B", 22)))
			.contains("Factorable.create(\"B\", 22)");
	}

	@Test
	@ExtendWith(ClasspathResourceExtension.class)
	public void testCodeSerializerWithExternalHints(ExtensibleClassLoader loader) throws Exception {
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.generator.TestGeneratorProfile", "net.amygdalum.testrecorder.scenarios.FactoryTest$ExternalAnnotationGeneratorProfile".getBytes());

		CodeSerializer codeSerializer = factorySerializer();

		assertThat(codeSerializer.serialize(FactorableWithoutAnnotation.create(22, "B")))
				.contains("FactorableWithoutAnnotation.create(22, \"B\")");
	}

	private static CodeSerializer factorySerializer() {
		CodeSerializer codeSerializer = new CodeSerializer("net.amygdalum.testrecorder.scenarios");
		codeSerializer.getTypes().registerTypes(Factorable.class);
		codeSerializer.getTypes().registerTypes(FactorableWithoutAnnotation.class);
		return codeSerializer;
	}

	public static class ExternalAnnotationGeneratorProfile implements TestGeneratorProfile {

		@Override
		public List<CustomAnnotation> annotations() {
			return asList(new CustomAnnotation(FactorableWithoutAnnotation.class, new Factory() {
				
				@Override
				public Class<? extends Annotation> annotationType() {
					return Factory.class;
				}
				
				@Override
				public String method() {
					return "create";
				}
				
				@Override
				public Class<?> clazz() {
					return FactorableWithoutAnnotation.class;
				}

			}));
		}

		@Override
		public Class<? extends TestTemplate> template() {
			return JUnit4TestTemplate.class;
		}

	}

}