package net.amygdalum.testrecorder.generator;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
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

import net.amygdalum.testrecorder.TestrecorderThreadFactory;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.util.ClassDescriptor;
import net.amygdalum.testrecorder.util.Logger;

/**
 * A configurable SnapshotConsumer client that writes tests to the file system
 */
public class ScheduledTestGenerator implements SnapshotConsumer {

	private static final String RECORDED_TEST = "RecordedTest";

	private ExecutorService executor;

	private volatile CompletableFuture<Void> pipeline;

	private Map<ClassDescriptor, ClassGenerator> generators;

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

	private SetupGenerators setup;
	private MatcherGenerators matcher;
	private TestTemplate template;
	private List<CustomAnnotation> annotations;


	public ScheduledTestGenerator(AgentConfiguration config) {
		this(
			config.loadConfiguration(PerformanceProfile.class),
			config.loadOptionalConfiguration(TestGeneratorProfile.class).orElseGet(DefaultTestGeneratorProfile::new),
			config.loadConfigurations(SetupGenerator.class),
			config.loadConfigurations(MatcherGenerator.class));
	}

	@SuppressWarnings("rawtypes")
	public ScheduledTestGenerator(PerformanceProfile profile, TestGeneratorProfile generatorProfile, List<SetupGenerator> setup, List<MatcherGenerator> matcher) {
		this.executor = initExecutor(profile);

		this.generators = synchronizedMap(new LinkedHashMap<>());
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("starting code generation");
		}, executor);

		this.counterMaximum = -1;
		this.counter = 0;
		this.start = System.currentTimeMillis();
		this.generateTo = Paths.get("generated + " + start);

		this.setup = new SetupGenerators(new Adaptors().load(setup));
		this.matcher = new MatcherGenerators(new Adaptors().load(matcher));
		this.template = initTemplate(generatorProfile.template());
		this.annotations = generatorProfile.annotations();
	}

	private TestTemplate initTemplate(Class<? extends TestTemplate> template) {
		try {
			return template.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void reload(AgentConfiguration config) {
		reload(
			config.loadConfiguration(PerformanceProfile.class),
			config.loadOptionalConfiguration(TestGeneratorProfile.class).orElseGet(DefaultTestGeneratorProfile::new),
			config.loadConfigurations(SetupGenerator.class),
			config.loadConfigurations(MatcherGenerator.class));
	}

	@SuppressWarnings("rawtypes")
	public void reload(PerformanceProfile profile, TestGeneratorProfile generatorProfile, List<SetupGenerator> setup, List<MatcherGenerator> matcher) {
		this.executor = initExecutor(profile);

		this.generators = synchronizedMap(new LinkedHashMap<>());
		this.pipeline = this.pipeline.thenRunAsync(() -> {
			Logger.info("restarting code generation");
		}, executor);

		this.counterMaximum = -1;
		this.counter = 0;
		this.start = System.currentTimeMillis();
		this.generateTo = Paths.get("generated + " + start);

		this.setup = new SetupGenerators(new Adaptors().load(setup));
		this.matcher = new MatcherGenerators(new Adaptors().load(matcher));
		this.template = initTemplate(generatorProfile.template());
		this.annotations = generatorProfile.annotations();
	}

	private static ThreadPoolExecutor initExecutor(PerformanceProfile profile) {
		return new ThreadPoolExecutor(0, 1, profile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$consume"));
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
	public synchronized void accept(ContextSnapshot snapshot) {
		pipeline = this.pipeline.thenRunAsync(() -> {
			if (counterMaximum > 0 && counter >= counterMaximum) {
				return;
			}
			generatorFor(snapshot).generate(snapshot);
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

	private String computePackage(ClassDescriptor clazz) {
		String pkg = clazz.getPackage();
		if (pkg.startsWith("java.lang")) {
			pkg = "test.java.lang" + pkg.substring("java.lang".length());
		}
		return pkg;
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

	public void writeResults(Path dir) {
		for (ClassDescriptor clazz : generators.keySet()) {

			String rendered = renderTest(clazz);

			try {
				Path testfile = locateTestFile(dir, clazz);
				Logger.info("writing tests to " + testfile.toAbsolutePath());
				try (Writer writer = Files.newBufferedWriter(testfile, StandardCharsets.UTF_8, CREATE, WRITE, TRUNCATE_EXISTING)) {
					writer.write(rendered);
				}
			} catch (IOException e) {
				Logger.error("failed writing tests for " + rendered, e);
			}
		}
	}

	public void clearResults() {
		this.generators.clear();
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("listening for snapshots");
		}, executor);
	}

	private Path locateTestFile(Path dir, ClassDescriptor clazz) throws IOException {
		String pkg = computePackage(clazz);
		String className = generatorFor(clazz).getTestName();
		Path testpackage = dir.resolve(pkg.replace('.', '/'));

		Files.createDirectories(testpackage);

		return testpackage.resolve(className + ".java");
	}

	public Set<String> testsFor(Class<?> clazz) {
		return testsFor(ClassDescriptor.of(clazz));
	}

	public Set<String> testsFor(ClassDescriptor clazz) {
		ClassGenerator generator = generatorFor(clazz);
		return generator.getTests();
	}

	public ClassGenerator generatorFor(ContextSnapshot snapshot) {
		Class<?> thisType = baseType(snapshot.getThisType());
		while (thisType.getEnclosingClass() != null) {
			thisType = thisType.getEnclosingClass();
		}
		ClassDescriptor baseType = ClassDescriptor.of(thisType);
		return generatorFor(baseType);
	}

	public ClassGenerator generatorFor(ClassDescriptor clazz) {
		return generators.computeIfAbsent(clazz, this::newGenerator);
	}

	public ClassGenerator newGenerator(ClassDescriptor clazz) {
		return new ClassGenerator(setup, matcher, template, annotations, computePackage(clazz), computeClassName(clazz));
	}

	public RenderedTest renderTest(Class<?> clazz) {
		return new RenderedTest(clazz, renderTest(ClassDescriptor.of(clazz)));
	}

	private String renderTest(ClassDescriptor clazz) {
		ClassGenerator generator = generatorFor(clazz);
		return generator.render();
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

	public void dumpResults() {
		writeResults(generateTo);
		clearResults();
	}

}
