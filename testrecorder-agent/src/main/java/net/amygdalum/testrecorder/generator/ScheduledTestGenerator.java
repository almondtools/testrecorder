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
import java.nio.file.Paths;
import java.util.HashSet;
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

/**
 * A configurable SnapshotConsumer client that writes tests to the file system
 */
public class ScheduledTestGenerator implements SnapshotConsumer {

	private static final String RECORDED_TEST = "RecordedTest";

	private ExecutorService executor;

	private volatile CompletableFuture<Void> pipeline;

	private AgentConfiguration config;
	private Deserializer<Computation> setup;
	private Deserializer<Computation> matcher;
	private Map<ClassDescriptor, TestGeneratorContext> tests;

	private static volatile Set<ScheduledTestGenerator> dumpOnShutDown;

	/**
	 * specifies the path where test files are dumped
	 */
	protected Path generateTo;
	/**
	 * specifies how many tests will be dumped (surplus will be skipped)
	 */
	protected int counterMaximum;
	/**
	 * specifies the generic class name of a dumped test file.
	 * The argument {@code template} is a template string using characters and following template expressions:
	 *     ${class} - the name of the class under test
	 *     ${counter} - the number of the generated test
	 *     ${millis} - the time stamp corresponding to the generated test
	 */
	protected String classNameTemplate;
	/**
	 * specifies a time interval of latency between two test renderings. Any snapshot arriving at least this 
	 * time interval after the last dump will trigger a new dump. 
	 */
	protected long timeInterval;
	/**
	 * specifies a number of tests that should trigger a new dump. Every time the list of recorded snapshots
	 * exceeds this number a new dump will be triggered.
	 */
	protected int counterInterval;

	private long start;
	private int counter;

	public ScheduledTestGenerator(AgentConfiguration config) {
		this.config = config;
		PerformanceProfile performanceProfile = config.loadConfiguration(PerformanceProfile.class);
		this.executor = new ThreadPoolExecutor(0, 1, performanceProfile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$consume"));

		this.setup = new SetupGenerators(config);
		this.matcher = new MatcherGenerators(config);

		this.tests = synchronizedMap(new LinkedHashMap<>());
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("starting code generation");
		}, executor);

		this.counterMaximum = -1;
		this.counter = 0;
		this.start = System.currentTimeMillis();
		this.generateTo = Paths.get("generated + " + start);
	}

	/**
	 * specifies that all pending tests should be dumped at shutdown time
	 * @param shutDown true if pending tests should be dumped at shutdown, false otherwise 
	 */
	protected synchronized void dumpOnShutdown(boolean shutDown) {
		if (dumpOnShutDown == null) {
			dumpOnShutDown = new HashSet<>();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					if (dumpOnShutDown != null) {
						for (ScheduledTestGenerator gen : dumpOnShutDown) {
							gen.shutdown(true);
						}
					}
				}

			}, "$generate-shutdown"));
		}
		if (shutDown) {
			dumpOnShutDown.add(this);
		} else {
			dumpOnShutDown.remove(this);
		}
	}

	@Override
	public void accept(ContextSnapshot snapshot) {
		pipeline = this.pipeline.thenRunAsync(() -> {
			if (counterMaximum > 0 && counter >= counterMaximum) {
				return;
			}

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
			counter++;
			if (counterInterval > 0 && counter % counterInterval == 0) {
				dumpResults();
			}

			long oldStart = start;
			start = System.currentTimeMillis();
			if (timeInterval > 0 && start - oldStart >= timeInterval) {
				dumpResults();
			}
		}, executor).exceptionally(e -> {
			Logger.error("failed generating test for " + snapshot.getMethodName() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
			return null;
		});
	}

	public void dumpResults() {
		writeResults(generateTo);
		clearResults();
	}

	public String computeClassName(ClassDescriptor clazz) {
		if (classNameTemplate == null) {
			return clazz.getSimpleName() + RECORDED_TEST;
		}
		return classNameTemplate
			.replace("${class}", clazz.getSimpleName())
			.replace("${counter}", String.valueOf(counter))
			.replace("${millis}", String.valueOf(start));
	}

	public void setSetup(Deserializer<Computation> setup) {
		this.setup = setup;
	}

	public void setMatcher(Deserializer<Computation> matcher) {
		this.matcher = matcher;
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
			Logger.info("listening for snapshots");
		}, executor);
	}

	private Path locateTestFile(Path dir, ClassDescriptor clazz) throws IOException {
		String pkg = clazz.getPackage();
		String className = getContext(clazz).getTestName();
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

	public ScheduledTestGenerator await() {
		this.pipeline.join();
		return this;
	}

	public void shutdown(boolean dumpFiles) {
		this.pipeline.join();
		this.executor.shutdown();
		if (dumpFiles) {
			writeResults(generateTo);
		}
	}

}
