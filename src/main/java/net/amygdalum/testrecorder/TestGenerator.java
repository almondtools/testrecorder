package net.amygdalum.testrecorder;

import static java.lang.Character.toUpperCase;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedMap;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Computation.variable;
import static net.amygdalum.testrecorder.deserializers.Templates.annotation;
import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignFieldStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.captureException;
import static net.amygdalum.testrecorder.deserializers.Templates.expressionStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldAccess;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.deserializers.Templates.returnStatement;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.ContextSnapshot.AnnotatedValue;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.MockedInteractions;
import net.amygdalum.testrecorder.deserializers.TreeAnalyzer;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;
import net.amygdalum.testrecorder.evaluator.SerializedValueEvaluator;
import net.amygdalum.testrecorder.hints.AnnotateGroupExpression;
import net.amygdalum.testrecorder.hints.AnnotateTimestamp;
import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.util.AnnotatedBy;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.util.Triple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class TestGenerator implements SnapshotConsumer {

	private static final Set<Class<?>> LITERAL_TYPES = new HashSet<>(Arrays.asList(
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class, String.class));

	private static final String RECORDED_TEST = "RecordedTest";

	private static final String TEST_FILE = "package <package>;\n\n"
		+ "<imports: {pkg | import <pkg>;\n}>"
		+ "\n\n\n"
		+ "@SuppressWarnings(\"unused\")\n"
		+ "public class <className> {\n"
		+ "\n"
		+ "  <fields; separator=\"\\n\">\n"
		+ "\n"
		+ "  <before>\n"
		+ "\n"
		+ "  <methods; separator=\"\\n\">"
		+ "\n}";

	private static final String BEFORE_TEMPLATE = "@Before\n"
		+ "public void before() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	private static final String TEST_TEMPLATE = "@Test\n"
		+ "<annotations:{annotation | <annotation>\n}>"
		+ "public void test<testName>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	private static final String BEGIN_ARRANGE = "\n//Arrange";
	private static final String BEGIN_ACT = "\n//Act";
	private static final String BEGIN_ASSERT = "\n//Assert";

	private ExecutorService executor;

	private volatile CompletableFuture<Void> pipeline;

	private DeserializerFactory setup;
	private DeserializerFactory matcher;
	private Map<ClassDescriptor, TestGeneratorContext> tests;
	private Set<String> fields;

	public TestGenerator() {
		executor = Executors.newSingleThreadExecutor(new TestrecorderThreadFactory("$consume"));

		this.setup = new SetupGenerators.Factory();
		this.matcher = new MatcherGenerators.Factory();

		this.tests = synchronizedMap(new LinkedHashMap<>());
		this.fields = new LinkedHashSet<>();
		this.pipeline = CompletableFuture.runAsync(() -> {
			System.out.println("starting code generation");
		}, executor);
	}

	@Override
	public void close() {
		executor.shutdown();
	}

	public String generateBefore(List<String> statements) {
		ST test = new ST(BEFORE_TEMPLATE);
		test.add("statements", statements);
		return test.render();
	}

	public void setSetup(DeserializerFactory setup) {
		this.setup = setup;
	}

	public void setMatcher(DeserializerFactory matcher) {
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
			TestGeneratorContext context = tests.computeIfAbsent(baseType, key -> new TestGeneratorContext(key));
			MethodGenerator methodGenerator = new MethodGenerator(context.size(), context.getTypes())
				.analyze(snapshot)
				.generateArrange()
				.generateAct()
				.generateAssert();

			context.add(methodGenerator.generateTest());
		}, executor).exceptionally(e -> {
			System.out.println("failed generating test for " + snapshot.getMethodName() + ": " + e.getClass().getSimpleName() + " " + e.getMessage());
			e.printStackTrace();
			return null;
		});
	}

	public void writeResults(Path dir) {
		for (ClassDescriptor clazz : tests.keySet()) {

			String rendered = renderTest(clazz);

			try {
				Path testfile = locateTestFile(dir, clazz);
				System.out.println("writing tests to " + testfile);
				try (Writer writer = Files.newBufferedWriter(testfile, StandardCharsets.UTF_8, CREATE, WRITE, TRUNCATE_EXISTING)) {
					writer.write(rendered);
				}
			} catch (IOException e) {
				System.out.println(rendered);
			}
		}
	}

	public void clearResults() {
		this.tests.clear();
		this.fields = new LinkedHashSet<>();
		this.pipeline = CompletableFuture.runAsync(() -> {
			System.out.println("starting code generation");
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
		return tests.getOrDefault(clazz, new TestGeneratorContext(clazz));
	}

	public String renderTest(Class<?> clazz) {
		return renderTest(ClassDescriptor.of(clazz));
	}

	public String renderTest(ClassDescriptor clazz) {
		TestGeneratorContext context = getContext(clazz);

		ST file = new ST(TEST_FILE);
		file.add("package", context.getPackage());
		file.add("className", computeClassName(clazz));
		file.add("fields", fields);
		file.add("before", computeBefore(context));
		file.add("methods", context.getTests());
		file.add("imports", context.getImports());

		return file.render();
	}

	private String computeBefore(TestGeneratorContext context) {
		TypeManager types = context.getTypes();
		types.registerType(Before.class);

		ServiceLoader<TestRecorderAgentInitializer> loader = ServiceLoader.load(TestRecorderAgentInitializer.class);

		List<String> statements = new ArrayList<>();
		for (TestRecorderAgentInitializer initializer : loader) {
			types.registerType(initializer.getClass());
			String initObject = newObject(types.getConstructorTypeName(initializer.getClass()));
			String initStmt = callMethodStatement(initObject, "run");
			statements.add(initStmt);
		}
		return generateBefore(statements);
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

	public void andThen(Runnable runnable) {
		this.pipeline.thenRun(runnable).join();
	}

	private class MethodGenerator {

		private LocalVariableNameGenerator locals;

		private int no;
		private ContextSnapshot snapshot;
		private DeserializerContext context;
		private TypeManager types;
		private MockedInteractions mocked;

		private List<String> statements;

		private String base;
		private List<String> args;
		private String result;
		private String error;

		public MethodGenerator(int no, TypeManager types) {
			this.no = no;
			this.types = types;
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public MethodGenerator analyze(ContextSnapshot snapshot) {
			this.snapshot = snapshot;
			this.context = computeInitialContext(snapshot);
			this.mocked = new MockedInteractions(setup, matcher, snapshot.getSetupInput(), snapshot.getExpectOutput());
			return this;
		}

		private DeserializerContext computeInitialContext(ContextSnapshot snapshot) {
			DeserializerContext context = new DeserializerContext();
			TreeAnalyzer collector = new TreeAnalyzer();

			Optional.ofNullable(snapshot.getSetupThis())
				.ifPresent(self -> collector.addSeed(self));
			Optional.ofNullable(snapshot.getExpectThis())
				.ifPresent(self -> collector.addSeed(self));

			Arrays.stream(snapshot.getSetupArgs())
				.filter(Objects::nonNull)
				.forEach(arg -> collector.addSeed(arg));
			Arrays.stream(snapshot.getExpectArgs())
				.filter(Objects::nonNull)
				.forEach(arg -> collector.addSeed(arg));

			Optional.ofNullable(snapshot.getExpectResult())
				.ifPresent(result -> collector.addSeed(result));

			Optional.ofNullable(snapshot.getExpectException())
				.ifPresent(exception -> collector.addSeed(exception));

			Arrays.stream(snapshot.getSetupGlobals())
				.filter(Objects::nonNull)
				.forEach(global -> collector.addGlobalSeed(global));
			Arrays.stream(snapshot.getExpectGlobals())
				.filter(Objects::nonNull)
				.forEach(global -> collector.addGlobalSeed(global));

			snapshot.getSetupInput().stream()
				.forEach(input -> collector.addInputSeed(input));

			snapshot.getExpectOutput().stream()
				.forEach(output -> collector.addOutputSeed(output));

			return collector.analyze(context);
		}

		public MethodGenerator generateArrange() {
			statements.add(BEGIN_ARRANGE);

			Deserializer<Computation> setupCode = setup.create(locals, types, mocked);

			Computation setupThis = snapshot.getSetupThis() != null
				? snapshot.getSetupThis().accept(setupCode, context)
				: variable(types.getVariableTypeName(types.wrapHidden(snapshot.getThisType())), null);
			setupThis.getStatements()
				.forEach(statements::add);

			AnnotatedValue[] snapshotSetupArgs = snapshot.getAnnotatedSetupArgs();
			List<Computation> setupArgs = Stream.of(snapshotSetupArgs)
				.map(arg -> arg.value.accept(setupCode, context.newWithHints(arg.annotations)))
				.collect(toList());

			setupArgs.stream()
				.flatMap(arg -> arg.getStatements().stream())
				.forEach(statements::add);

			List<Computation> setupGlobals = Stream.of(snapshot.getSetupGlobals())
				.map(global -> assignGlobal(global.getDeclaringClass(), global.getName(), global.getValue().accept(setupCode, context)))
				.collect(toList());

			setupGlobals.stream()
				.flatMap(arg -> arg.getStatements().stream())
				.forEach(statements::add);

			this.base = setupThis.isStored()
				? setupThis.getValue()
				: assign(snapshot.getSetupThis().getType(), setupThis.getValue());
			Pair<Computation, AnnotatedValue>[] arguments = Pair.zip(setupArgs.toArray(new Computation[0]), snapshotSetupArgs);
			this.args = Stream.of(arguments)
				.map(arg -> arg.getElement1().isStored()
					? arg.getElement1().getValue()
					: assign(arg.getElement2().value.getResultType(), arg.getElement1().getValue()))
				.collect(toList());

			return this;
		}

		private Computation assignGlobal(Class<?> clazz, String name, Computation global) {
			List<String> statements = new ArrayList<>(global.getStatements());
			String base = types.getVariableTypeName(clazz);
			statements.add(assignFieldStatement(base, name, global.getValue()));
			String value = fieldAccess(base, name);
			return variable(value, global.getType(), statements);
		}

		public MethodGenerator generateAct() {
			statements.add(BEGIN_ACT);

			Type resultType = snapshot.getResultType();
			String methodName = snapshot.getMethodName();
			SerializedValue exception = snapshot.getExpectException();

			MethodGenerator gen;
			if (exception != null) {
				gen = new MethodGenerator(no, types).analyze(snapshot);
			} else {
				gen = this;
			}
			String statement = callMethod(base, methodName, args);
			if (resultType != void.class) {
				result = gen.assign(resultType, statement, true);
			} else {
				gen.execute(statement);
			}
			if (exception != null) {
				List<String> exceptionBlock = new ArrayList<>();
				exceptionBlock.addAll(gen.statements);
				if (resultType != void.class) {
					exceptionBlock.add(returnStatement(result));
				}
				error = capture(exceptionBlock, exception.getType());
			}

			return this;
		}

		public MethodGenerator generateAssert() {
			types.staticImport(Assert.class, "assertThat");
			statements.add(BEGIN_ASSERT);

			if (error == null) {
				Annotation[] resultAnnotation = snapshot.getResultAnnotation();
				Stream.of(snapshot.getExpectResult())
					.flatMap(res -> generateResultAssert(types, res, resultAnnotation, result))
					.forEach(statements::add);
			} else {
				Stream.of(snapshot.getExpectException())
					.flatMap(e -> generateExceptionAssert(types, e, error))
					.forEach(statements::add);
			}

			boolean thisChanged = compare(snapshot.getSetupThis(), snapshot.getExpectThis());
			SerializedValue snapshotExpectThis = snapshot.getExpectThis();
			Stream.of(snapshotExpectThis)
				.flatMap(self -> generateThisAssert(types, self, base, thisChanged))
				.forEach(statements::add);

			Boolean[] argsChanged = compare(snapshot.getSetupArgs(), snapshot.getExpectArgs());
			AnnotatedValue[] snapshotExpectArgs = snapshot.getAnnotatedExpectArgs();
			Triple<AnnotatedValue, String, Boolean>[] arguments = Triple.zip(snapshotExpectArgs, args.toArray(new String[0]), argsChanged);
			Stream.of(arguments)
				.flatMap(arg -> generateArgumentAssert(types, arg.getElement1(), arg.getElement2(), arg.getElement3()))
				.forEach(statements::add);

			Boolean[] globalsChanged = compare(snapshot.getExpectGlobals(), snapshot.getExpectGlobals());
			SerializedField[] snapshotExpectGlobals = snapshot.getExpectGlobals();
			Pair<SerializedField, Boolean>[] globals = Pair.zip(snapshotExpectGlobals, globalsChanged);
			Stream.of(globals)
				.flatMap(global -> generateGlobalAssert(types, global.getElement1(), global.getElement2()))
				.forEach(statements::add);

			return this;
		}

		private Stream<String> generateResultAssert(TypeManager types, SerializedValue result, Annotation[] resultAnnotation, String expression) {
			if (result == null) {
				return Stream.empty();
			}
			Computation matcherExpression = result.accept(matcher.create(locals, types, mocked), context.newWithHints(resultAnnotation));
			if (matcherExpression == null) {
				return Stream.empty();
			}
			return createAssertion(matcherExpression, expression).stream();
		}

		private Stream<String> generateExceptionAssert(TypeManager types, SerializedValue exception, String expression) {
			if (exception == null) {
				return Stream.empty();
			}
			Computation matcherExpression = exception.accept(matcher.create(locals, types, mocked), context);
			return createAssertion(matcherExpression, expression).stream();
		}

		private Stream<String> generateThisAssert(TypeManager types, SerializedValue value, String expression, Boolean changed) {
			if (value == null) {
				return Stream.empty();
			}
			Computation matcherExpression = value.accept(matcher.create(locals, types, mocked), context);
			return createAssertion(matcherExpression, expression, changed).stream();
		}

		private Stream<String> generateArgumentAssert(TypeManager types, AnnotatedValue value, String expression, Boolean changed) {
			if (value == null || value.value instanceof SerializedLiteral) {
				return Stream.empty();
			}
			Computation matcherExpression = value.value.accept(matcher.create(locals, types, mocked), context.newWithHints(value.annotations));
			if (matcherExpression == null) {
				return Stream.empty();
			}
			return createAssertion(matcherExpression, expression, changed).stream();
		}

		private Stream<String> generateGlobalAssert(TypeManager types, SerializedField value, Boolean changed) {
			Computation matcherExpression = value.getValue().accept(matcher.create(locals, types, mocked), context);
			String expression = fieldAccess(types.getVariableTypeName(value.getDeclaringClass()), value.getName());
			return createAssertion(matcherExpression, expression, changed).stream();
		}

		private Boolean[] compare(SerializedField[] s, SerializedField[] e) {
			Boolean[] changes = new Boolean[s.length];
			for (int i = 0; i < changes.length; i++) {
				changes[i] = compare(s[i].getValue(), e[i].getValue());
			}
			return changes;
		}

		private Boolean[] compare(SerializedValue[] s, SerializedValue[] e) {
			Boolean[] changes = new Boolean[s.length];
			for (int i = 0; i < changes.length; i++) {
				changes[i] = compare(s[i], e[i]);
			}
			return changes;
		}

		private boolean compare(SerializedValue s, SerializedValue e) {
			if (s == e) {
				return true;
			} else if (s == null || e == null) {
				return false;
			}
			Computation sc = s.accept(setup.create(new LocalVariableNameGenerator(), new TypeManager()), context);
			Computation ec = e.accept(setup.create(new LocalVariableNameGenerator(), new TypeManager()), context);
			return !ec.getValue().equals(sc.getValue())
				|| !ec.getStatements().equals(sc.getStatements());
		}

		private List<String> createAssertion(Computation matcher, String exp, Boolean changed) {
			List<String> statements = new ArrayList<>();

			statements.addAll(matcher.getStatements());

			if (changed == null) {
				statements.add(callLocalMethodStatement("assertThat", exp, matcher.getValue()));
			} else if (changed) {
				statements.add(callLocalMethodStatement("assertThat", asLiteral("expected change:"), exp, matcher.getValue()));
			} else {
				statements.add(callLocalMethodStatement("assertThat", asLiteral("expected no change, but was:"), exp, matcher.getValue()));
			}

			return statements;
		}

		private List<String> createAssertion(Computation matcher, String exp) {
			List<String> statements = new ArrayList<>();

			statements.addAll(matcher.getStatements());

			statements.add(callLocalMethodStatement("assertThat", exp, matcher.getValue()));

			return statements;
		}

		public String assign(Type type, String value) {
			return assign(type, value, false);
		}

		public String assign(Type type, String value, boolean force) {
			if (isLiteral(type) && !force) {
				return value;
			} else {
				types.registerImport(baseType(type));
				String name = locals.fetchName(type);

				statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, value));

				return name;
			}
		}

		public void execute(String value) {
			statements.add(expressionStatement(value));
		}

		public String capture(List<String> capturedStatements, Type type) {
			types.staticImport(Throwables.class, "capture");
			String name = locals.fetchName(type);

			String exceptionType = types.getRawClass(type);
			String capture = captureException(capturedStatements, exceptionType);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, capture));

			return name;
		}

		private boolean isLiteral(Type type) {
			return isPrimitive(type)
				|| LITERAL_TYPES.contains(type);
		}

		public String generateTest() {
			ST test = new ST(TEST_TEMPLATE);
			test.add("annotations", annotations());
			test.add("testName", testName());
			test.add("statements", statements);
			return test.render();
		}

		private List<String> annotations() {
			return Stream.of(snapshot.getResultAnnotation())
				.map(annotation -> transferAnnotation(annotation))
				.filter(Objects::nonNull)
				.collect(toList());
		}

		private String transferAnnotation(Annotation annotation) {
			if (annotation instanceof AnnotateTimestamp) {
				return generateTimestampAnnotation(((AnnotateTimestamp) annotation).format());
			} else if (annotation instanceof AnnotateGroupExpression) {
				return generateGroupAnnotation(((AnnotateGroupExpression) annotation).expression());
			}
			return null;
		}

		private String generateTimestampAnnotation(String format) {
			String date = new SimpleDateFormat(format).format(new Date(snapshot.getTime()));
			types.registerImport(AnnotatedBy.class);
			return annotation(types.getRawTypeName(AnnotatedBy.class), asList(
				new Pair<>("name", asLiteral("timestamp")),
				new Pair<>("value", asLiteral(date))));
		}

		private String generateGroupAnnotation(String expression) {
			types.registerImport(AnnotatedBy.class);
			Optional<SerializedValue> serialized = new SerializedValueEvaluator(expression).applyTo(snapshot.getSetupThis());
			return serialized
				.filter(value -> value instanceof SerializedLiteral)
				.map(value -> ((SerializedLiteral) value).getValue())
				.map(value -> annotation(types.getRawTypeName(AnnotatedBy.class), asList(
					new Pair<>("name", asLiteral("group")),
					new Pair<>("value", asLiteral(value.toString())))))
				.orElse(null);
		}

		private String testName() {
			String testName = snapshot.getMethodName();

			return toUpperCase(testName.charAt(0)) + testName.substring(1) + no;
		}

	}

}
