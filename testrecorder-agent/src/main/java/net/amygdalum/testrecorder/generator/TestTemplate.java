package net.amygdalum.testrecorder.generator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.types.TypeManager;

public interface TestTemplate {

	Class<?>[] getTypes();

	String testClass(String methodName, TypeManager types, Map<String, String> setups, Set<String> tests);

	String setupMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements);

	String testMethod(String methodName, TypeManager types, List<String> annotations, List<String> statements);

}
