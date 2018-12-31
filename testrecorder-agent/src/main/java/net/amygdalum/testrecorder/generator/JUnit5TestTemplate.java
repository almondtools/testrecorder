package net.amygdalum.testrecorder.generator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.types.TypeManager;

public class JUnit5TestTemplate implements TestTemplate {

	private static final String CLASS_TEMPLATE = ""
		+ "package <package>;\n\n"
		+ "<imports: {pkg | import <pkg>;\n}>"
		+ "\n\n\n"
		+ "@SuppressWarnings(\"unused\")\n"
		+ "public class <className> {\n"
		+ "\n"
		+ "  <setup; separator=\"\\n\">\n"
		+ "\n"
		+ "  <methods; separator=\"\\n\">"
		+ "\n}";

	private static final String SETUP_TEMPLATE = ""
		+ "<annotations;separator=\"\\n\">\n"
		+ "public void <name>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	private static final String TEST_TEMPLATE = ""
		+ "@Test\n"
		+ "<annotations:{annotation | <annotation>\n}>"
		+ "public void test<testName>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	@Override
	public Class<?>[] getTypes() {
		return new Class[] { BeforeEach.class, AfterEach.class, Test.class };
	}

	@Override
	public String testClass(String testName, TypeManager types, Map<String, String> setups, Set<String> tests) {
		ST file = new ST(CLASS_TEMPLATE);
		file.add("package", types.getPackage());
		file.add("className", testName);
		file.add("setup", setups.values());
		file.add("methods", tests);
		file.add("imports", types.getImports());

		return file.render();
	}

	@Override
	public String setupMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements) {
		ST test = new ST(SETUP_TEMPLATE);
		test.add("annotations", annotations);
		test.add("name", methodName);
		test.add("statements", statements);
		return test.render();
	}

	@Override
	public String testMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements) {
		ST test = new ST(TEST_TEMPLATE);
		test.add("annotations", annotations);
		test.add("testName", methodName);
		test.add("statements", statements);
		return test.render();
	}
}
