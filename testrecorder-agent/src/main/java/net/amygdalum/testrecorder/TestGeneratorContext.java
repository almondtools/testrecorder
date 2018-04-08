package net.amygdalum.testrecorder;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.types.TypeManager;

public class TestGeneratorContext {

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
	private TypeManager types;
	private Map<String,String> setups;
	private Set<String> tests;
	
	public TestGeneratorContext(ClassDescriptor key, String testName) {
		this.testName = testName;
		this.types = new DeserializerTypeManager(key.getPackage());
		this.setups = new LinkedHashMap<>();
		this.tests = new LinkedHashSet<>();

		types.registerTypes(Test.class);
	}
	
	public String getTestName() {
		return testName;
	}
	
	public TypeManager getTypes() {
		return types;
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
