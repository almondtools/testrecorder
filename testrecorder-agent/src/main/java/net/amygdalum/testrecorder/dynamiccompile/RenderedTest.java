package net.amygdalum.testrecorder.dynamiccompile;

public class RenderedTest {

	private ClassLoader classLoader;
	private String testCode;

	public RenderedTest(Class<?> testedClass, String testCode) {
		this(testedClass == null ? RenderedTest.class.getClassLoader() : testedClass.getClassLoader(), testCode);
	}
	
	public RenderedTest(ClassLoader classLoader, String testCode) {
		this.classLoader = classLoader;
		this.testCode = testCode;
	}
	
	public String getTestCode() {
		return testCode;
	}

	public ClassLoader getTestClassLoader() {
		return classLoader;
	}
	
	@Override
	public String toString() {
		return testCode;
	}

}
