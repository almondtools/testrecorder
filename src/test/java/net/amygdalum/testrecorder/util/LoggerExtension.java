package net.amygdalum.testrecorder.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class LoggerExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private ByteArrayOutputStream info;
	private ByteArrayOutputStream warn;
	private ByteArrayOutputStream error;

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		info = new ByteArrayOutputStream();
		warn = new ByteArrayOutputStream();
		error = new ByteArrayOutputStream();
		Logger.setINFO(new Logger(new PrintStream(info)));
		Logger.setWARN(new Logger(new PrintStream(warn)));
		Logger.setERROR(new Logger(new PrintStream(error)));
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Logger.resetINFO();
		Logger.resetWARN();
		Logger.resetERROR();
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		LogLevel logLevel = parameterContext.getParameter().getAnnotation(LogLevel.class);
		switch (logLevel.value()) {
		case "error":
			return error;
		case "warn":
			return warn;
		case "info":
		default:
			return info;
		}

	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return ByteArrayOutputStream.class.isAssignableFrom(parameterContext.getParameter().getType())
			&& parameterContext.getParameter().getAnnotation(LogLevel.class) != null;
	}

}
