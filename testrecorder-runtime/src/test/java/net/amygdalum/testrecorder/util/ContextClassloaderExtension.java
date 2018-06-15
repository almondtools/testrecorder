package net.amygdalum.testrecorder.util;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ContextClassloaderExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback {

	private Thread all;
	private ClassLoader backupAllClassLoader;
	private Thread current;
	private ClassLoader backupClassLoader;

	@Override
	public void beforeAll(ExtensionContext arg0) throws Exception {
		all = Thread.currentThread();
		backupAllClassLoader = all.getContextClassLoader();
	}

	@Override
	public void afterAll(ExtensionContext arg0) throws Exception {
		if (all != null) {
			all.setContextClassLoader(backupAllClassLoader);
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		current = Thread.currentThread();
		backupClassLoader = current.getContextClassLoader();
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		if (current != null) {
			current.setContextClassLoader(backupClassLoader);
		}
	}

}
