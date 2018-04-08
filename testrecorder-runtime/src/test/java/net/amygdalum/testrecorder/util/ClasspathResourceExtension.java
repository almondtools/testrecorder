package net.amygdalum.testrecorder.util;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ClasspathResourceExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private ClassLoader backupClassLoader;
	private ExtensibleClassLoader classLoader;
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		backupClassLoader = Thread.currentThread().getContextClassLoader();
		classLoader = new ExtensibleClassLoader(backupClassLoader);
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Thread.currentThread().setContextClassLoader(backupClassLoader);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return ExtensibleClassLoader.class.isAssignableFrom(parameterContext.getParameter().getType());
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return classLoader;
	}

}
