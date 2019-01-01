package net.amygdalum.testrecorder.test;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.function.Consumer;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.generator.RenderedTest;
import net.amygdalum.testrecorder.util.Instantiations;

public class JUnit5TestsRun implements Consumer<RenderedTest> {

	private SoftAssertions softly;
	private String[] code;
	private DynamicClassCompiler compiler;

	public JUnit5TestsRun(String... code) {
		this.softly = new SoftAssertions();
		this.code = code;
		this.compiler = new DynamicClassCompiler();
	}

	public static JUnit5TestsRun testsRun() {
		return new JUnit5TestsRun();
	}

	public static JUnit5TestsRun testsRunWith(String... code) {
		return new JUnit5TestsRun(code);
	}

	@Override
	public void accept(RenderedTest test) {
		ClassLoader backupLoader = Thread.currentThread().getContextClassLoader();
		try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			for (String nextCode : code) {
				compiler.compile(nextCode, clazz.getClassLoader());
			}
			Thread.currentThread().setContextClassLoader(clazz.getClassLoader());

			LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectClass(clazz))
				.build();

			LauncherConfig config = LauncherConfig.builder()
				.enableTestEngineAutoRegistration(false)
			    .enableTestExecutionListenerAutoRegistration(false)
			    .addTestEngines(new JupiterTestEngine())
				.build();
			Launcher launcher = LauncherFactory.create(config);

			Instantiations.resetInstatiations();
			launcher.execute(request, new TestExecutionListener() {
				@Override
				public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
					if (testIdentifier.isContainer()) {
						return;
					}
					switch (testExecutionResult.getStatus()) {
					case SUCCESSFUL:
						return;
					case FAILED:
					case ABORTED:
					default:
						testExecutionResult.getThrowable().ifPresent(t -> {
							softly.fail("compiled successfully but got test failures : %s", t.getMessage());
						});
						return;
					}

				}
			});

		} catch (DynamicClassCompilerException e) {
			softly.fail(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				softly.fail(msg);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(backupLoader);
		}
		softly.assertAll();
	}

}
