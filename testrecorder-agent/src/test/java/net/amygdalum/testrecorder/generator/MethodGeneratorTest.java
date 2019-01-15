package net.amygdalum.testrecorder.generator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.xrayinterface.XRayInterface.xray;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.types.VirtualMethodSignature;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.values.SerializedObject;

public class MethodGeneratorTest {

	private TypeManager types;
	private TestTemplate template;
	private SetupGenerators setup;
	private MatcherGenerators matcher;

	@BeforeEach
	void before() throws Exception {
		AgentConfiguration config = defaultConfig();
		types = new DeserializerTypeManager();
		template = new JUnit4TestTemplate();
		setup = new SetupGenerators(new Adaptors().load(config.loadConfigurations(SetupGenerator.class)));
		matcher = new MatcherGenerators(new Adaptors().load(config.loadConfigurations(MatcherGenerator.class)));
	}

	@Nested
	class testGenerateAct {
		@Test
		void withResult() throws Exception {
			MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher, template, emptyList());
			methodGenerator.analyze(snapshotWithResult());
			xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");

			methodGenerator.generateAct();

			assertThat(methodGenerator.generateTest()).containsWildcardPattern("String string? = var.getAttribute();");
		}

		@Test
		void withoutResult() throws Exception {
			MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher, template, emptyList());
			methodGenerator.analyze(snapshotNoResult());
			xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");
			xray(methodGenerator).to(OpenMethodGenerator.class).setArgs(asList("\"newstr\""));

			methodGenerator.generateAct();

			assertThat(methodGenerator.generateTest()).containsWildcardPattern("var.setAttribute(\"newstr\");");
		}

		@Test
		void withResultAndException() throws Exception {
			MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher, template, emptyList());
			methodGenerator.analyze(snapshotWithResultAndException());
			xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");

			methodGenerator.generateAct();

			assertThat(methodGenerator.generateTest()).containsWildcardPattern(""
				+ "RuntimeException runtimeException? = capture(() -> {"
				+ "String string? = var.getAttribute();"
				+ "return string?;"
				+ "}, RuntimeException.class);");
		}

		@Test
		void withoutResultAndException() throws Exception {
			MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher, template, emptyList());
			methodGenerator.analyze(snapshotNoResultAndException());
			xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");
			xray(methodGenerator).to(OpenMethodGenerator.class).setArgs(asList("\"newstr\""));

			methodGenerator.generateAct();

			assertThat(methodGenerator.generateTest()).containsWildcardPattern(""
				+ "RuntimeException runtimeException? = capture(() -> {"
				+ "var.setAttribute(\"newstr\");"
				+ "}, RuntimeException.class);");
		}
	}

	private ContextSnapshot snapshotWithResult() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, String.class, "getAttribute");
		FieldSignature attribute = new FieldSignature(Bean.class, String.class, "attribute");
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "str"))));
		snapshot.setSetupArgs();
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "str"))));
		snapshot.setExpectArgs();
		snapshot.setExpectResult(literal("str"));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}

	private ContextSnapshot snapshotNoResult() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, void.class, "setAttribute", String.class);
		FieldSignature attribute = new FieldSignature(Bean.class, String.class, "attribute");
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "str"))));
		snapshot.setSetupArgs(literal("newstr"));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "newstr"))));
		snapshot.setExpectArgs(literal("newstr"));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}

	private ContextSnapshot snapshotWithResultAndException() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, String.class, "getAttribute");
		FieldSignature attribute = new FieldSignature(Bean.class, String.class, "attribute");
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "str"))));
		snapshot.setSetupArgs();
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "str"))));
		snapshot.setExpectArgs();
		snapshot.setExpectException(objectOf(RuntimeException.class));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}

	private ContextSnapshot snapshotNoResultAndException() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, void.class, "setAttribute", String.class);
		FieldSignature attribute = new FieldSignature(Bean.class, String.class, "attribute");
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "str"))));
		snapshot.setSetupArgs(literal("newstr"));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(attribute, literal(String.class, "newstr"))));
		snapshot.setExpectArgs(literal("newstr"));
		snapshot.setExpectException(objectOf(RuntimeException.class));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}

	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new VirtualMethodSignature(new MethodSignature(declaringClass, resultType, methodName, argumentTypes)));
	}

	private SerializedObject objectOf(Class<?> type, SerializedField... fields) {
		SerializedObject setupThis = new SerializedObject(type);
		for (SerializedField field : fields) {
			setupThis.addField(field);
		}
		return setupThis;
	}

	interface OpenMethodGenerator {
		void setBase(String base);

		void setArgs(List<String> args);
	}
}
