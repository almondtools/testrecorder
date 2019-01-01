package net.amygdalum.testrecorder.test;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.function.Consumer;

import org.assertj.core.api.SoftAssertions;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.generator.RenderedTest;
import net.amygdalum.testrecorder.util.Instantiations;

public class JUnit5TestsFail implements Consumer<RenderedTest> {

	private SoftAssertions softly;
	private DynamicClassCompiler compiler;

	public JUnit5TestsFail() {
		softly = new SoftAssertions();
		compiler = new DynamicClassCompiler();
	}

	public static JUnit5TestsFail testsFail() {
		return new JUnit5TestsFail();
	}

	@Override
	public void accept(RenderedTest test) {
		ClassLoader backupLoader = Thread.currentThread().getContextClassLoader();
		try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			Thread.currentThread().setContextClassLoader(clazz.getClassLoader());

			LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectClass(clazz))
				.build();

			Launcher launcher = LauncherFactory.create();

			Instantiations.resetInstatiations();
			launcher.execute(request, new TestExecutionListener() {
				@Override
				public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
					switch (testExecutionResult.getStatus()) {
					case FAILED:
						return;
					case SUCCESSFUL:
						softly.fail("expected test failures but tests were successful");
						return;
					case ABORTED:
					default:
						softly.fail("expected test failures but tests were successful");
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
