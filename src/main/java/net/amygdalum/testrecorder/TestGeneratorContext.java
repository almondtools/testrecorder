package net.amygdalum.testrecorder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TypeManager;

public class TestGeneratorContext {

	private TypeManager types;
	private Set<String> tests;

	
	public TestGeneratorContext(Class<?> clazz) {
		this.types = new TypeManager(clazz.getPackage().getName());
		this.tests = new LinkedHashSet<>();

		types.registerTypes(Test.class);
	}
	
	public String getPackage() {
		return types.getPackage();
	}
	
	public TypeManager getTypes() {
		return types;
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

	public synchronized void add(String test) {
		tests.add(test);
	}
}
