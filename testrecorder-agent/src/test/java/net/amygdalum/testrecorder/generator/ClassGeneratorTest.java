package net.amygdalum.testrecorder.generator;

import static java.util.Collections.emptyList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.TestDeserializer;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.VirtualMethodSignature;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ClassGeneratorTest {

	private TestTemplate template;
	private ExtensibleClassLoader loader;
	private AgentConfiguration config;
	private ClassGenerator testGenerator;

	@BeforeEach
	void before() throws Exception {
		template = new JUnit4TestTemplate();
		loader = new ExtensibleClassLoader(TestGenerator.class.getClassLoader());
		config = defaultConfig().withLoader(loader);
	}

	@Nested
	class testGenerate {

		@Test
		void forSetup() throws Exception {
			DeserializerFactory setup = new TestDeserializer.Factory();
			DeserializerFactory matcher = new MatcherGenerators(new Adaptors().load(config.loadConfigurations(MatcherGenerator.class)));
			testGenerator = new ClassGenerator(setup, matcher, template, emptyList(), MyClass.class.getPackage().getName(), MyClass.class.getSimpleName());

			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.generate(snapshot);

			assertThat(testGenerator.getTests())
				.hasSize(1)
				.anySatisfy(test -> {
					assertThat(test).containsSubsequence("(net.amygdalum.testrecorder.generator.ClassGeneratorTest$MyClass/",
						"int field: 12",
						"intMethod((16))",
						"equalTo(22)",
						"int field = 8;");
				});

		}

		@Test
		void forMatcher() throws Exception {
			DeserializerFactory setup = new SetupGenerators(new Adaptors().load(config.loadConfigurations(SetupGenerator.class)));
			DeserializerFactory matcher = new TestDeserializer.Factory();
			testGenerator = new ClassGenerator(setup, matcher, template, emptyList(), MyClass.class.getPackage().getName(), MyClass.class.getSimpleName());

			ContextSnapshot snapshot = contextSnapshot(MyClass.class, int.class, "intMethod", int.class);
			FieldSignature field = new FieldSignature(MyClass.class, int.class, "field");
			snapshot.setSetupThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 12))));
			snapshot.setSetupArgs(literal(int.class, 16));
			snapshot.setSetupGlobals(new SerializedField[0]);
			snapshot.setExpectThis(objectOf(MyClass.class, new SerializedField(field, literal(int.class, 8))));
			snapshot.setExpectArgs(literal(int.class, 16));
			snapshot.setExpectResult(literal(int.class, 22));
			snapshot.setExpectGlobals(new SerializedField[0]);

			testGenerator.generate(snapshot);

			assertThat(testGenerator.getTests())
				.hasSize(1)
				.anySatisfy(test -> {
					assertThat(test).containsSubsequence(
						"int field = 12;",
						"intMethod(16);",
						"(22)",
						"(net.amygdalum.testrecorder.generator.ClassGeneratorTest$MyClass/",
						"int field: 8");
				});
		}

	}

	private static ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new VirtualMethodSignature(new MethodSignature(declaringClass, resultType, methodName, argumentTypes)));
	}

	private static SerializedObject objectOf(Class<MyClass> type, SerializedField... fields) {
		SerializedObject setupThis = new SerializedObject(type);
		for (SerializedField field : fields) {
			setupThis.addField(field);
		}
		return setupThis;
	}

	@SuppressWarnings("unused")
	private static class MyClass {

		private int field;

		public int intMethod(int arg) {
			return field + arg;
		}
	}

}
