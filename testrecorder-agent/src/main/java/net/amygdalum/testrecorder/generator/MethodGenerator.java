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
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.deserializers.Templates.expressionStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldAccess;
import static net.amygdalum.testrecorder.deserializers.Templates.returnStatement;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.util.Types.mostSpecialOf;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.MatcherAssert;
import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.MockedInteractions;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.ReferenceAnalyzer;
import net.amygdalum.testrecorder.deserializers.TreeAnalyzer;
import net.amygdalum.testrecorder.evaluator.SerializedValueEvaluator;
import net.amygdalum.testrecorder.hints.AnnotateGroupExpression;
import net.amygdalum.testrecorder.hints.AnnotateTimestamp;
import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.AnnotatedBy;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.util.Triple;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class MethodGenerator {

	private static final Set<Class<?>> LITERAL_TYPES = new HashSet<>(asList(
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
	private DeserializerFactory setup;
	private DeserializerFactory matcher;
	private List<CustomAnnotation> annotations;

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

	public MethodGenerator(int no, TypeManager types, DeserializerFactory setup, DeserializerFactory matcher, List<CustomAnnotation> annotations) {
		this.no = no;
		this.types = types;
		this.setup = setup;
		this.matcher = matcher;
		this.annotations = annotations;
		this.locals = new LocalVariableNameGenerator();
		this.statements = new ArrayList<>();
	}

	public MethodGenerator analyze(ContextSnapshot snapshot) {
		this.snapshot = snapshot;
		this.context = computeInitialContext(snapshot);
		this.mocked = new MockedInteractions(setup.newGenerator(context), matcher.newGenerator(context), snapshot.getSetupInput(), snapshot.getExpectOutput());
		return this;
	}

	private DefaultDeserializerContext computeInitialContext(ContextSnapshot snapshot) {
		DefaultDeserializerContext context = new DefaultDeserializerContext(types, locals);
		for (CustomAnnotation annotation : annotations) {
			context.addHint(annotation.getTarget(), annotation.getAnnotation());
		}

		TreeAnalyzer analyzer = new TreeAnalyzer()
			.addListener(new ReferenceAnalyzer(context));

		analyzer.analyze(snapshot);

		return context;
	}

	public MethodGenerator generateArrange() {
		statements.add(BEGIN_ARRANGE);

		Deserializer deserializer = setup.newGenerator(context);

		types.registerType(snapshot.getThisType());
		snapshot.streamSetupGlobals().forEach(global -> types.registerImport(global.getDeclaringClass()));

		Computation setupThis = snapshot.onSetupThis()
			.map(self -> prepareThis(self, snapshot.getThisType(), deserializer))
			.orElseGet(() -> prepareStatic(snapshot.getThisType()));
		setupThis.getStatements()
			.forEach(statements::add);

		List<Computation> setupArgs = snapshot.streamSetupArgs()
			.map(arg -> prepareArgument(arg, deserializer))
			.collect(toList());

		setupArgs.stream()
			.flatMap(arg -> arg.getStatements().stream())
			.forEach(statements::add);

		List<Computation> setupGlobals = snapshot.streamSetupGlobals()
			.map(global -> assignGlobal(global.getDeclaringClass(), global.getName(), global.getValue().accept(deserializer)))
			.collect(toList());

		setupGlobals.stream()
			.flatMap(arg -> arg.getStatements().stream())
			.forEach(statements::add);

		this.base = setupThis.isStored()
			? setupThis.getValue()
			: newLocal(snapshot.getThisType(), setupThis.getValue());
		this.args = setupArgs.stream()
			.map(arg -> arg.getValue())
			.collect(toList());
		statements.addAll(mocked.prepare(context));

		return this;
	}

	private Computation prepareThis(SerializedValue self, Type type, Deserializer deserializer) {
		Computation computation = self.accept(deserializer);
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

	private Computation prepareStatic(Type type) {
		return variable(types.getVariableTypeName(types.wrapHidden(type)), null);
	}

	private Computation prepareArgument(SerializedArgument arg, Deserializer deserializer) {
		Type argType = arg.getType();
		SerializedValue argValue = arg.getValue();
		Type type = mostSpecialOf(argValue.getUsedTypes()).orElse(Object.class);
		Computation computation = arg.accept(deserializer);
		if (!assignableTypes(argType, type)) {
			types.registerType(argType);
			String value = cast(types.getVariableTypeName(argType), computation.getValue());
			computation = Computation.expression(value, argType, computation.getStatements());
		}
		if (computation.isStored()) {
			return computation;
		} else if (isLiteral(type)) {
			return computation;
		} else {
			List<String> statements = new ArrayList<>(computation.getStatements());
			String value = computation.getValue();

			types.registerType(type);
			String name = locals.fetchName(type);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(argType), name, value));

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

		MethodGenerator gen = snapshot.onExpectException()
			.map(e -> new MethodGenerator(no, types, setup, matcher, annotations).analyze(snapshot))
			.orElse(this);

		String statement = callMethod(base, methodName, args);
		if (resultType != void.class) {
			result = gen.newLocal(resultType, statement, true);
		} else {
			gen.execute(statement);
		}
		snapshot.onExpectException().ifPresent(exception -> {
			List<String> exceptionBlock = new ArrayList<>();
			exceptionBlock.addAll(gen.statements);
			if (resultType != void.class) {
				exceptionBlock.add(returnStatement(result));
			}
			error = capture(exceptionBlock, exception.getType());
		});

		return this;
	}

	public MethodGenerator generateAssert() {
		types.staticImport(MatcherAssert.class, "assertThat");
		statements.add(BEGIN_ASSERT);

		statements.addAll(mocked.verify(locals, types, context));

		if (error == null) {
			snapshot.streamExpectResult()
				.flatMap(res -> generateResultAssert(types, res, result))
				.forEach(statements::add);
		} else {
			snapshot.streamExpectException()
				.flatMap(e -> generateExceptionAssert(types, e, error))
				.forEach(statements::add);
		}

		boolean thisChanged = snapshot.onThis()
			.map((before, after) -> compare(before, after), other -> false)
			.orElse(true);
		snapshot.streamExpectThis()
			.flatMap(self -> generateThisAssert(types, self, base, thisChanged))
			.forEach(statements::add);

		Boolean[] argsChanged = compare(snapshot.getSetupArgs(), snapshot.getExpectArgs());
		SerializedArgument[] snapshotExpectArgs = snapshot.getExpectArgs();
		Triple<SerializedArgument, String, Boolean>[] arguments = Triple.zip(snapshotExpectArgs, args.toArray(new String[0]), argsChanged);
		Stream.of(arguments)
			.flatMap(arg -> generateArgumentAssert(types, arg.getElement1(), arg.getElement2(), arg.getElement3()))
			.forEach(statements::add);

		Boolean[] globalsChanged = compare(snapshot.getSetupGlobals(), snapshot.getExpectGlobals());
		SerializedField[] snapshotExpectGlobals = snapshot.getExpectGlobals();
		Pair<SerializedField, Boolean>[] globals = Pair.zip(snapshotExpectGlobals, globalsChanged);
		Stream.of(globals)
			.flatMap(global -> generateGlobalAssert(types, global.getElement1(), global.getElement2()))
			.forEach(statements::add);

		return this;
	}

	private Stream<String> generateResultAssert(TypeManager types, SerializedResult result, String expression) {
		Deserializer deserializer = matcher.newGenerator(context.newIsolatedContext(types, locals));

		Computation matcherExpression = result.accept(deserializer);
		if (matcherExpression == null) {
			return Stream.empty();
		}
		return createAssertion(matcherExpression, expression).stream();
	}

	private Stream<String> generateExceptionAssert(TypeManager types, SerializedValue exception, String expression) {
		Deserializer deserializer = matcher.newGenerator(context.newIsolatedContext(types, locals));

		if (exception == null) {
			return Stream.empty();
		}
		Computation matcherExpression = exception.accept(deserializer);
		return createAssertion(matcherExpression, expression).stream();
	}

	private Stream<String> generateThisAssert(TypeManager types, SerializedValue value, String expression, boolean changed) {
		Deserializer deserializer = matcher.newGenerator(context.newIsolatedContext(types, locals));

		Computation matcherExpression = value.accept(deserializer);
		return createAssertion(matcherExpression, expression, changed).stream();
	}

	private Stream<String> generateArgumentAssert(TypeManager types, SerializedArgument arg, String expression, boolean changed) {
		Deserializer deserializer = matcher.newGenerator(context.newIsolatedContext(types, locals));

		if (arg == null || arg.getValue() instanceof SerializedLiteral || arg.getValue() instanceof SerializedImmutableType) {
			return Stream.empty();
		}
		Computation matcherExpression = arg.accept(deserializer);
		if (matcherExpression == null) {
			return Stream.empty();
		}
		return createAssertion(matcherExpression, expression, changed).stream();
	}

	private Stream<String> generateGlobalAssert(TypeManager types, SerializedField value, boolean changed) {
		Deserializer deserializer = matcher.newGenerator(context.newIsolatedContext(types, locals));

		Computation matcherExpression = value.getValue().accept(deserializer);
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

	private Boolean[] compare(SerializedArgument[] s, SerializedArgument[] e) {
		Boolean[] changes = new Boolean[s.length];
		for (int i = 0; i < changes.length; i++) {
			changes[i] = compare(s[i].getValue(), e[i].getValue());
		}
		return changes;
	}

	private boolean compare(SerializedValue s, SerializedValue e) {
		Deserializer deserializer = setup.newGenerator(DefaultDeserializerContext.empty());
		if (s == e) {
			return true;
		} else if (s == null || e == null) {
			return false;
		}
		Computation sc = s.accept(deserializer);
		Computation ec = e.accept(deserializer);
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

	public String newLocal(Type type, String value) {
		return newLocal(type, value, false);
	}

	public String newLocal(Type type, String value, boolean force) {
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
		return snapshot.streamExpectResult()
			.flatMap(result -> Stream.concat(
				context.getHints(result, AnnotateTimestamp.class)
					.map(annotation -> generateTimestampAnnotation(annotation.format())),
				context.getHints(result, AnnotateGroupExpression.class)
					.map(annotation -> generateGroupAnnotation(annotation.expression()))))
			.collect(toList());
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
		return snapshot.onSetupThis()
			.flatMap(self -> new SerializedValueEvaluator(expression).applyTo(self))
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