package net.amygdalum.testrecorder;

import java.util.LinkedHashSet;
import java.util.List;
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

	private static final String SETUP_TEMPLATE = "<annotations>\n"
		+ "public void <name>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";


	private String testName;
	private TypeManager types;
	private Set<String> setups;
	private Set<String> tests;
	
	public TestGeneratorContext(ClassDescriptor key, String testName) {
		this.testName = testName;
		this.types = new DeserializerTypeManager(key.getPackage());
		this.setups = new LinkedHashSet<>();
		this.tests = new LinkedHashSet<>();

		types.registerTypes(Test.class);
	}
	
	public String getPackage() {
		return types.getPackage();
	}
	
	public TypeManager getTypes() {
		return types;
	}
	
	public Set<String> getSetups() {
		return setups;
	}

	public synchronized Set<String> getTests() {
		return tests;
	}

	public List<String> getImports() {
		return types.getImports();
	}

	public synchronized int size() {
		return tests.size();
	}

	public synchronized void addSetup(List<String> annotations, String name, List<String> statements) {
		ST test = new ST(SETUP_TEMPLATE);
		test.add("annotations", annotations);
		test.add("name", name);
		test.add("statements", statements);
		setups.add(test.render());
	}

	public synchronized void add(String test) {
		tests.add(test);
	}

	public String render() {
		ST file = new ST(TEST_FILE);
		file.add("package", types.getPackage());
		file.add("className", testName);
		file.add("setup", setups);
		file.add("methods", tests);
		file.add("imports", types.getImports());

		return file.render();
	}

}
