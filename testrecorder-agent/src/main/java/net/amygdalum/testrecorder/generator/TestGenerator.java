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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.amygdalum.testrecorder.ClassDescriptor;
import net.amygdalum.testrecorder.SnapshotManager;
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
import net.amygdalum.testrecorder.util.Logger;

public class TestGenerator implements SnapshotConsumer {

	private static final String RECORDED_TEST = "RecordedTest";

	private ExecutorService executor;

	private volatile CompletableFuture<Void> pipeline;

	private Map<ClassDescriptor, ClassGenerator> generators;

	private SetupGenerators setup;
	private MatcherGenerators matcher;
	private TestTemplate template;
	private List<CustomAnnotation> annotations;


	public TestGenerator(AgentConfiguration config) {
		this(
			config.loadConfiguration(PerformanceProfile.class),
			config.loadOptionalConfiguration(TestGeneratorProfile.class).orElseGet(DefaultTestGeneratorProfile::new),
			config.loadConfigurations(SetupGenerator.class),
			config.loadConfigurations(MatcherGenerator.class));
	}

	@SuppressWarnings("rawtypes")
	public TestGenerator(PerformanceProfile profile, TestGeneratorProfile generatorProfile, List<SetupGenerator> setup, List<MatcherGenerator> matcher) {
		this.executor = initExecutor(profile);

		this.generators = synchronizedMap(new LinkedHashMap<>());
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("starting code generation");
		}, executor);

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

		this.setup = new SetupGenerators(new Adaptors().load(setup));
		this.matcher = new MatcherGenerators(new Adaptors().load(matcher));
		this.annotations = generatorProfile.annotations();
	}

	private static ThreadPoolExecutor initExecutor(PerformanceProfile profile) {
		return new ThreadPoolExecutor(0, 1, profile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$consume"));
	}

	public static TestGenerator fromRecorded() {
		SnapshotConsumer consumer = SnapshotManager.MANAGER.getMethodConsumer();
		if (!(consumer instanceof TestGenerator)) {
			return null;
		}
		TestGenerator testGenerator = (TestGenerator) consumer;
		return testGenerator.await();
	}

	@Override
	public synchronized void accept(ContextSnapshot snapshot) {
		pipeline = this.pipeline.thenRunAsync(() -> generatorFor(snapshot).generate(snapshot), executor).exceptionally(e -> {
			Logger.error("failed generating test for " + snapshot.getMethodName() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
			return null;
		});
	}

	public String computeClassName(ClassDescriptor clazz) {
		return clazz.getSimpleName() + RECORDED_TEST;
	}

	public void writeResults(Path dir) {
		for (ClassDescriptor clazz : generators.keySet()) {

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
		this.generators.clear();
		this.pipeline = CompletableFuture.runAsync(() -> {
			Logger.info("listening for snapshots");
		}, executor);
	}

	private Path locateTestFile(Path dir, ClassDescriptor clazz) throws IOException {
		String pkg = clazz.getPackage();
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
		return new ClassGenerator(setup, matcher, template, annotations, clazz.getPackage(), computeClassName(clazz));
	}

	public RenderedTest renderTest(Class<?> clazz) {
		return new RenderedTest(clazz, renderTest(ClassDescriptor.of(clazz)));
	}

	private String renderTest(ClassDescriptor clazz) {
		ClassGenerator generator = generatorFor(clazz);
		return generator.render();
	}

	public TestGenerator await() {
		this.pipeline.join();
		return this;
	}

}
