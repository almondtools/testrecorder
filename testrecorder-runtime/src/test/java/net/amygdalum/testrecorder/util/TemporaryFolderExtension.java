package net.amygdalum.testrecorder.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class TemporaryFolderExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private List<TemporaryFolder> folders;

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		folders = new ArrayList<>();
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		for (TemporaryFolder folder : folders) {
			folder.close();
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType() == TemporaryFolder.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		try {
			TemporaryFolder folder = new TemporaryFolder();
			folder.prepare();
			folders.add(folder);
			return folder;
		} catch (IOException e) {
			throw new ParameterResolutionException("cannot prepare temporary folder", e);
		}
	}

}
