package net.amygdalum.testrecorder.generator;

import static java.lang.Character.toUpperCase;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.annotation;
import static net.amygdalum.testrecorder.deserializers.Templates.assignFieldStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.captureException;
import static net.amygdalum.testrecorder.deserializers.Templates.expressionStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldAccess;
import static net.amygdalum.testrecorder.deserializers.Templates.returnStatement;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.util.Types.mostSpecialOf;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Assert;
import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.ContextSnapshot;
import net.amygdalum.testrecorder.MockedInteractions;
import net.amygdalum.testrecorder.ContextSnapshot.AnnotatedValue;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.TreeAnalyzer;
import net.amygdalum.testrecorder.evaluator.SerializedValueEvaluator;
import net.amygdalum.testrecorder.hints.AnnotateGroupExpression;
import net.amygdalum.testrecorder.hints.AnnotateTimestamp;
import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.AnnotatedBy;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.util.Triple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class MethodGenerator {

	private static final Set<Class<?>> LITERAL_TYPES = new HashSet<>(Arrays.asList(
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class, String.class));

	private static final String TEST_TEMPLATE = "@Test\n"
		+ "<annotations:{annotation | <annotation>\n}>"
		+ "public void test<testName>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	private static final String BEGIN_ARRANGE = "\n//Arrange";
	private static final String BEGIN_ACT = "\n//Act";
	private static final String BEGIN_ASSERT = "\n//Assert";

	private LocalVariableNameGenerator locals;
	private Deserializer<Computation> setup;
	private Deserializer<Computation> matcher;

	private int no;
	private ContextSnapshot snapshot;
	private DefaultDeserializerContext context;
	private TypeManager types;
	private MockedInteractions mocked;

	private List<String> statements;

	private String base;
	private List<String> args;
	private String result;
	private String error;

	public MethodGenerator(int no, TypeManager types, Deserializer<Computation> setup, Deserializer<Computation> matcher) {
		this.no = no;
		this.types = types;
		this.setup = setup;
		this.matcher = matcher;
		this.locals = new LocalVariableNameGenerator();
		this.statements = new ArrayList<>();
	}

	public MethodGenerator analyze(ContextSnapshot snapshot) {
		this.snapshot = snapshot;
		this.context = computeInitialContext(snapshot);
		this.mocked = new MockedInteractions(setup, matcher, snapshot.getSetupInput(), snapshot.getExpectOutput());
		return this;
	}

	private DefaultDeserializerContext computeInitialContext(ContextSnapshot snapshot) {
		DefaultDeserializerContext context = new DefaultDeserializerContext(types, locals);
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

		types.registerType(snapshot.getThisType());
		Stream.of(snapshot.getSetupGlobals()).forEach(global -> types.registerImport(global.getDeclaringClass()));

		Computation setupThis = prepareThis(snapshot.getSetupThis(), snapshot.getThisType());
		setupThis.getStatements()
			.forEach(statements::add);

		AnnotatedValue[] snapshotSetupArgs = snapshot.getAnnotatedSetupArgs();
		List<Computation> setupArgs = Stream.of(snapshotSetupArgs)
			.map(arg -> prepareArgument(arg.value, arg.annotations))

			.collect(toList());

		setupArgs.stream()
			.flatMap(arg -> arg.getStatements().stream())
			.forEach(statements::add);

		List<Computation> setupGlobals = Stream.of(snapshot.getSetupGlobals())
			.map(global -> assignGlobal(global.getDeclaringClass(), global.getName(), global.getValue().accept(setup, context)))
			.collect(toList());

		setupGlobals.stream()
			.flatMap(arg -> arg.getStatements().stream())
			.forEach(statements::add);

		this.base = setupThis.isStored()
			? setupThis.getValue()
			: assign(snapshot.getSetupThis().getType(), setupThis.getValue());
		this.args = setupArgs.stream()
			.map(arg -> arg.getValue())
			.collect(toList());

		statements.addAll(mocked.prepare(context));

		return this;
	}

	private Computation prepareThis(SerializedValue self, Type type) {
		Computation computation = self != null
			? self.accept(setup, context)
			: variable(types.getVariableTypeName(types.wrapHidden(type)), null);
		if (computation.isStored()) {
			return computation;
		} else if (isLiteral(type)) {
			return computation;
		} else {
			List<String> statements = new ArrayList<>(computation.getStatements());
			String value = computation.getValue();

			types.registerType(type);
			String name = locals.fetchName(type);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, value));

			return variable(name, type, statements);
		}
	}

	private Computation prepareArgument(SerializedValue argValue, Annotation[] annotations) {
		Type type = mostSpecialOf(argValue.getUsedTypes()).orElse(Object.class); 
		Computation computation = argValue.accept(setup, context.newWithHints(annotations));
		if (computation.isStored()) {
			return computation;
		} else if (isLiteral(type)) {
			return computation;
		} else {
			List<String> statements = new ArrayList<>(computation.getStatements());
			String value = computation.getValue();

			types.registerType(type);
			String name = locals.fetchName(type);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(type), name, value));

			return variable(name, type, statements);
		}
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
			gen = new MethodGenerator(no, types, setup, matcher).analyze(snapshot);
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

		statements.addAll(mocked.verify(locals, types, context));

		return this;
	}

	private Stream<String> generateResultAssert(TypeManager types, SerializedValue result, Annotation[] resultAnnotation, String expression) {
		if (result == null) {
			return Stream.empty();
		}
		Computation matcherExpression = result.accept(matcher, new DefaultDeserializerContext(types, locals).newWithHints(resultAnnotation));
		if (matcherExpression == null) {
			return Stream.empty();
		}
		return createAssertion(matcherExpression, expression).stream();
	}

	private Stream<String> generateExceptionAssert(TypeManager types, SerializedValue exception, String expression) {
		if (exception == null) {
			return Stream.empty();
		}
		Computation matcherExpression = exception.accept(matcher, new DefaultDeserializerContext(types, locals));
		return createAssertion(matcherExpression, expression).stream();
	}

	private Stream<String> generateThisAssert(TypeManager types, SerializedValue value, String expression, boolean changed) {
		if (value == null) {
			return Stream.empty();
		}
		Computation matcherExpression = value.accept(matcher, new DefaultDeserializerContext(types, locals));
		return createAssertion(matcherExpression, expression, changed).stream();
	}

	private Stream<String> generateArgumentAssert(TypeManager types, AnnotatedValue value, String expression, boolean changed) {
		if (value == null || value.value instanceof SerializedLiteral || value.value instanceof SerializedImmutableType) {
			return Stream.empty();
		}
		Computation matcherExpression = value.value.accept(matcher, new DefaultDeserializerContext(types, locals).newWithHints(value.annotations));
		if (matcherExpression == null) {
			return Stream.empty();
		}
		return createAssertion(matcherExpression, expression, changed).stream();
	}

	private Stream<String> generateGlobalAssert(TypeManager types, SerializedField value, boolean changed) {
		Computation matcherExpression = value.getValue().accept(matcher, new DefaultDeserializerContext(types, locals));
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
		Computation sc = s.accept(setup, new DefaultDeserializerContext());
		Computation ec = e.accept(setup, new DefaultDeserializerContext());
		return !ec.getValue().equals(sc.getValue())
			|| !ec.getStatements().equals(sc.getStatements());
	}

	private List<String> createAssertion(Computation matcher, String exp, boolean changed) {
		List<String> statements = new ArrayList<>();

		statements.addAll(matcher.getStatements());

		if (changed) {
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