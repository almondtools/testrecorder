package net.amygdalum.testrecorder.generator;

import static java.util.Arrays.asList;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.TypeManager;

public class ClassGenerator {

	private static final String TEST_FILE = "package <package>;\n\n"
		+ "<imports: {pkg | import <pkg>;\n}>"
		+ "\n\n\n"
		+ "@SuppressWarnings(\"unused\")\n"
		+ "public class <className> {\n"
		+ "\n"
		+ "  <setup; separator=\"\\n\">\n"
		+ "\n"
		+ "  <methods; separator=\"\\n\">"
		+ "\n}";

	private String testName;
	private DeserializerFactory setup;
	private DeserializerFactory matcher;
	private TypeManager types;
	private Map<String, String> setups;
	private Set<String> tests;

	public ClassGenerator(DeserializerFactory setup, DeserializerFactory matcher, List<TestRecorderAgentInitializer> initializers, String pkg, String testName) {
		this.testName = testName;
		this.setup = setup;
		this.matcher = matcher;
		this.types = new DeserializerTypeManager(pkg);
		this.setups = new LinkedHashMap<>();
		this.tests = new LinkedHashSet<>();

		types.registerTypes(Test.class);

		if (!initializers.isEmpty()) {
			SetupGenerator setupGenerator = new SetupGenerator(types, "initialize", asList(Before.class));

			for (TestRecorderAgentInitializer initializer : initializers) {
				setupGenerator = setupGenerator.generateInitialize(initializer);
			}

			addSetup("initialize", setupGenerator.generateSetup());
		}
	}

	public void setSetup(DeserializerFactory setup) {
		this.setup = setup;
	}

	public void setMatcher(DeserializerFactory matcher) {
		this.matcher = matcher;
	}

	public String getTestName() {
		return testName;
	}

	public synchronized Set<String> getTests() {
		return tests;
	}

	public synchronized int size() {
		return tests.size();
	}

	public synchronized void addSetup(String key, String setup) {
		setups.put(key, setup);
	}

	public synchronized void add(String test) {
		tests.add(test);
	}

	public void generate(ContextSnapshot snapshot) {
		if (snapshot.hasSetupInput() || snapshot.hasExpectOutput()) {
			SetupGenerator setupGenerator = new SetupGenerator(types, "resetFakeIO", asList(Before.class, After.class))
				.generateReset();
			addSetup("resetFakeIO", setupGenerator.generateSetup());
		}

		MethodGenerator methodGenerator = new MethodGenerator(size(), types, setup, matcher)
			.analyze(snapshot)
			.generateArrange()
			.generateAct()
			.generateAssert();

		add(methodGenerator.generateTest());
	}

	public String render() {
		ST file = new ST(TEST_FILE);
		file.add("package", types.getPackage());
		file.add("className", testName);
		file.add("setup", setups.values());
		file.add("methods", tests);
		file.add("imports", types.getImports());

		return file.render();
	}

}
