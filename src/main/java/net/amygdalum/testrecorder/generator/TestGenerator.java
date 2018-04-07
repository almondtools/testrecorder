package net.amygdalum.testrecorder.generator;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedMap;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import net.amygdalum.testrecorder.ClassDescriptor;
import net.amygdalum.testrecorder.ContextSnapshot;
import net.amygdalum.testrecorder.SetupGenerator;
import net.amygdalum.testrecorder.SnapshotConsumer;
import net.amygdalum.testrecorder.SnapshotManager;
import net.amygdalum.testrecorder.TestGeneratorContext;
import net.amygdalum.testrecorder.TestrecorderThreadFactory;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.util.Logger;

public class TestGenerator implements SnapshotConsumer {

	private static final String RECORDED_TEST = "RecordedTest";

	private ExecutorService executor;

	private volatile CompletableFuture<Void> pipeline;

	private AgentConfiguration config;
	private Deserializer<Computation> setup;
	private Deserializer<Computation> matcher;
	private Map<ClassDescriptor, TestGeneratorContext> tests;


	public TestGenerator(AgentConfiguration config) {
		this.config = config;
		PerformanceProfile performanceProfile = config.loadConfiguration(PerformanceProfile.class);
		this.executor = new ThreadPoolExecutor(0, 1, performanceProfile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$consume"));

		this.setup = new SetupGenerators(config);
		this.matcher = new MatcherGenerators(config);

		this.tests = synchronizedMap(new LinkedHashMap<>());
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("starting code generation");
		}, executor);
	}

	public void setSetup(Deserializer<Computation> setup) {
		this.setup = setup;
	}

	public void setMatcher(Deserializer<Computation> matcher) {
		this.matcher = matcher;
	}

	@Override
	public synchronized void accept(ContextSnapshot snapshot) {
		pipeline = this.pipeline.thenRunAsync(() -> {
			Class<?> thisType = baseType(snapshot.getThisType());
			while (thisType.getEnclosingClass() != null) {
				thisType = thisType.getEnclosingClass();
			}
			ClassDescriptor baseType = ClassDescriptor.of(thisType);
			TestGeneratorContext context = getContext(baseType);

			if (!snapshot.getSetupInput().isEmpty() || !snapshot.getExpectOutput().isEmpty()) {
				SetupGenerator setupGenerator = new SetupGenerator(context.getTypes(), "resetFakeIO", asList(Before.class, After.class))
					.generateReset();
				context.addSetup("resetFakeIO", setupGenerator.generateSetup());
			}

			MethodGenerator methodGenerator = new MethodGenerator(context.size(), context.getTypes(), setup, matcher)
				.analyze(snapshot)
				.generateArrange()
				.generateAct()
				.generateAssert();

			context.add(methodGenerator.generateTest());
		}, executor).exceptionally(e -> {
			Logger.error("failed generating test for " + snapshot.getMethodName() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
			return null;
		});
	}

	public void writeResults(Path dir) {
		for (ClassDescriptor clazz : tests.keySet()) {

			String rendered = renderTest(clazz);

			try {
				Path testfile = locateTestFile(dir, clazz);
				Logger.info("writing tests to " + testfile);
				try (Writer writer = Files.newBufferedWriter(testfile, StandardCharsets.UTF_8, CREATE, WRITE, TRUNCATE_EXISTING)) {
					writer.write(rendered);
				}
			} catch (IOException e) {
				Logger.error("failed writing tests for " + rendered, e);
			}
		}
	}

	public void clearResults() {
		this.tests.clear();
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("starting code generation");
		}, executor);
	}

	private Path locateTestFile(Path dir, ClassDescriptor clazz) throws IOException {
		String pkg = clazz.getPackage();
		String className = computeClassName(clazz);
		Path testpackage = dir.resolve(pkg.replace('.', '/'));

		Files.createDirectories(testpackage);

		return testpackage.resolve(className + ".java");
	}

	public Set<String> testsFor(Class<?> clazz) {
		return testsFor(ClassDescriptor.of(clazz));
	}

	public Set<String> testsFor(ClassDescriptor clazz) {
		TestGeneratorContext context = getContext(clazz);
		return context.getTests();
	}

	public TestGeneratorContext getContext(ClassDescriptor clazz) {
		return tests.computeIfAbsent(clazz, this::newContext);
	}

	public TestGeneratorContext newContext(ClassDescriptor clazz) {
		TestGeneratorContext context = new TestGeneratorContext(clazz, computeClassName(clazz));
		List<TestRecorderAgentInitializer> initializers = config.loadConfigurations(TestRecorderAgentInitializer.class);
		if (!initializers.isEmpty()) {
			SetupGenerator setupGenerator = new SetupGenerator(context.getTypes(), "initialize", asList(Before.class));
		
			for (TestRecorderAgentInitializer initializer : initializers) {
				setupGenerator = setupGenerator.generateInitialize(initializer);
			}
		
			context.addSetup("initialize", setupGenerator.generateSetup());
		}
		return context;
	}

	public RenderedTest renderTest(Class<?> clazz) {
		return new RenderedTest(clazz, renderTest(ClassDescriptor.of(clazz)));
	}

	private String renderTest(ClassDescriptor clazz) {
		TestGeneratorContext context = getContext(clazz);
		return context.render();

	}

	public String computeClassName(ClassDescriptor clazz) {
		return clazz.getSimpleName() + RECORDED_TEST;
	}

	public static TestGenerator fromRecorded() {
		SnapshotConsumer consumer = SnapshotManager.MANAGER.getMethodConsumer();
		if (!(consumer instanceof TestGenerator)) {
			return null;
		}
		TestGenerator testGenerator = (TestGenerator) consumer;
		return testGenerator.await();
	}

	public TestGenerator await() {
		this.pipeline.join();
		return this;
	}

}
