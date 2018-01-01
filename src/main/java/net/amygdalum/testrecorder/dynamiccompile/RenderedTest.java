package net.amygdalum.testrecorder.dynamiccompile;

public class RenderedTest {

	private Class<?> testedClass;
	private String testCode;

	public RenderedTest(Class<?> testedClass, String testCode) {
		this.testedClass = testedClass;
		this.testCode = testCode;
	}
	
	public Class<?> getTestedClass() {
		return testedClass;
	}
	
	public String getTestCode() {
		return testCode;
	}

	public ClassLoader getTestClassLoader() {
		return testedClass == null ? getClass().getClassLoader() : testedClass.getClassLoader();
	}

}
