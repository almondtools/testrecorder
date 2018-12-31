package net.amygdalum.testrecorder.generator;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.TypeManager;

public class ClassGenerator {

	private String testName;
	private TestTemplate template;
	private DeserializerFactory setup;
	private DeserializerFactory matcher;
	private List<CustomAnnotation> annotations;
	private TypeManager types;
	private Map<String, String> setups;
	private Set<String> tests;

	public ClassGenerator(DeserializerFactory setup, DeserializerFactory matcher, TestTemplate template, List<CustomAnnotation> annotations, String pkg, String testName) {
		this.testName = testName;
		this.template = template;
		this.setup = setup;
		this.matcher = matcher;
		this.annotations = annotations;
		this.types = new DeserializerTypeManager(pkg);
		this.setups = new LinkedHashMap<>();
		this.tests = new LinkedHashSet<>();

		types.registerTypes(template.getTypes());
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
			SetupGenerator setupGenerator = new SetupGenerator(types, "resetFakeIO", template, annotations)
				.generateReset();
			addSetup("resetFakeIO", setupGenerator.generateSetup());
		}

		MethodGenerator methodGenerator = new MethodGenerator(size(), types, setup, matcher, template, annotations)
			.analyze(snapshot)
			.generateArrange()
			.generateAct()
			.generateAssert();

		add(methodGenerator.generateTest());
	}

	public String render() {
		return template.testClass(testName, types, setups, tests);
	}

}
