package net.amygdalum.testrecorder.generator;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.ContextSnapshot;
import net.amygdalum.testrecorder.MethodSignature;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.generator.MethodGenerator;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.xrayinterface.XRayInterface;

public class MethodGeneratorTest {

	private AgentConfiguration config;
	private TypeManager types;
	private SetupGenerators setup;
	private MatcherGenerators matcher;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		types = new DeserializerTypeManager();
		setup = new SetupGenerators(config);
		matcher = new MatcherGenerators(config);
	}
	
	@Test
	public void testGenerateActWithResult() throws Exception {
		MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher);
		methodGenerator.analyze(snapshotWithResult());
		XRayInterface.xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");
		
		methodGenerator.generateAct();
		
		assertThat(methodGenerator.generateTest()).containsWildcardPattern("String string? = var.getAttribute();");
	}

	@Test
	public void testGenerateActNoResult() throws Exception {
		MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher);
		methodGenerator.analyze(snapshotNoResult());
		XRayInterface.xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");
		XRayInterface.xray(methodGenerator).to(OpenMethodGenerator.class).setArgs(asList("\"newstr\""));
		
		methodGenerator.generateAct();
		
		assertThat(methodGenerator.generateTest()).containsWildcardPattern("var.setAttribute(\"newstr\");");
	}

	@Test
	public void testGenerateActWithResultAndException() throws Exception {
		MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher);
		methodGenerator.analyze(snapshotWithResultAndException());
		XRayInterface.xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");
		
		methodGenerator.generateAct();
		
		assertThat(methodGenerator.generateTest()).containsWildcardPattern(""
			+ "RuntimeException runtimeException? = capture(() -> {"
			+ "String string? = var.getAttribute();"
			+ "return string?;"
			+ "}, RuntimeException.class);");
	}

	@Test
	public void testGenerateActNoResultAndException() throws Exception {
		MethodGenerator methodGenerator = new MethodGenerator(1, types, setup, matcher);
		methodGenerator.analyze(snapshotNoResultAndException());
		XRayInterface.xray(methodGenerator).to(OpenMethodGenerator.class).setBase("var");
		XRayInterface.xray(methodGenerator).to(OpenMethodGenerator.class).setArgs(asList("\"newstr\""));
		
		methodGenerator.generateAct();
		
		assertThat(methodGenerator.generateTest()).containsWildcardPattern(""
			+ "RuntimeException runtimeException? = capture(() -> {"
			+ "var.setAttribute(\"newstr\");"
			+ "}, RuntimeException.class);");
	}

	private ContextSnapshot snapshotWithResult() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, String.class, "getAttribute");
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "str"))));
		snapshot.setSetupArgs();
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "str"))));
		snapshot.setExpectArgs();
		snapshot.setExpectResult(literal("str"));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}

	private ContextSnapshot snapshotNoResult() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, void.class, "setAttribute", String.class);
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "str"))));
		snapshot.setSetupArgs(literal("newstr"));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "newstr"))));
		snapshot.setExpectArgs(literal("newstr"));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}
	
	private ContextSnapshot snapshotWithResultAndException() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, String.class, "getAttribute");
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "str"))));
		snapshot.setSetupArgs();
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "str"))));
		snapshot.setExpectArgs();
		snapshot.setExpectException(objectOf(RuntimeException.class));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}
	
	private ContextSnapshot snapshotNoResultAndException() {
		ContextSnapshot snapshot = contextSnapshot(Bean.class, void.class, "setAttribute", String.class);
		snapshot.setSetupThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "str"))));
		snapshot.setSetupArgs(literal("newstr"));
		snapshot.setSetupGlobals(new SerializedField[0]);
		snapshot.setExpectThis(objectOf(Bean.class, new SerializedField(Bean.class, "attribute", String.class, literal(String.class, "newstr"))));
		snapshot.setExpectArgs(literal("newstr"));
		snapshot.setExpectException(objectOf(RuntimeException.class));
		snapshot.setExpectGlobals(new SerializedField[0]);
		return snapshot;
	}
	
	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new MethodSignature(declaringClass, new Annotation[0], resultType, methodName, new Annotation[0][0], argumentTypes));
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
