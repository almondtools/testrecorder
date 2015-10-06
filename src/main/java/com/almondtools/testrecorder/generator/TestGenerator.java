package com.almondtools.testrecorder.generator;

import static com.almondtools.testrecorder.generator.TypeHelper.getBase;
import static com.almondtools.testrecorder.generator.TypeHelper.getSimpleName;
import static com.almondtools.testrecorder.generator.TypeHelper.isPrimitive;
import static java.lang.Character.toUpperCase;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.stringtemplate.v4.ST;

import com.almondtools.testrecorder.GeneratedSnapshot;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.visitors.Computation;
import com.almondtools.testrecorder.visitors.ImportManager;
import com.almondtools.testrecorder.visitors.LocalVariableNameGenerator;
import com.almondtools.testrecorder.visitors.ObjectToMatcherCode;
import com.almondtools.testrecorder.visitors.ObjectToSetupCode;

public class TestGenerator implements Consumer<GeneratedSnapshot> {

	private static final Set<Class<?>> IMMUTABLE_TYPES = new HashSet<>(Arrays.asList(
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class,
		String.class));

	private static final String TEST_FILE = "package <package>;\n\n"
		+ "<imports: {pkg | import <pkg>;\n}>"
		+ "\n\n\n"
		+ "public class <className> {\n"
		+ "\n"
		+ "  <methods; separator=\"\\n\">"
		+ "\n}";

	private static final String TEST_TEMPLATE = "@Test\n"
		+ "public void test<testName>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	private static final String ASSIGN_STMT = "<type> <name> = <value>;";
	private static final String EXPRESSION_STMT = "<value>;";

	private static final String BEGIN_ARRANGE = "\n//Arrange";
	private static final String BEGIN_ACT = "\n//Act";
	private static final String BEGIN_ASSERT = "\n//Assert";

	private static final String CALL_EXPRESSION = "<base>.<method>(<args; separator=\", \">)";

	private ImportManager imports;
	private Map<Class<?>, Set<String>> tests;

	public TestGenerator() {
		this.imports = new ImportManager();
		this.imports.registerImport(Test.class);

		this.tests = new LinkedHashMap<>();
	}

	public Set<String> getTests(Class<?> clazz) {
		return tests.get(clazz);
	}

	@Override
	public void accept(GeneratedSnapshot snapshot) {
		Set<String> localtests = tests.computeIfAbsent(getBase(snapshot.getThisType()), key -> new LinkedHashSet<>());

		MethodGenerator methodGenerator = new MethodGenerator(snapshot, localtests.size())
			.generateArrange()
			.generateAct()
			.generateAssert();

		localtests.add(methodGenerator.generateTest());
	}

	public void writeTests(Path dir) {
		for (Class<?> clazz : tests.keySet()) {

			String rendered = renderTest(clazz);

			try {
				Path testfile = locateTestFile(dir, clazz);
				try (Writer writer = Files.newBufferedWriter(testfile)) {
					writer.write(rendered);
				}
			} catch (IOException e) {
				System.out.println(rendered);
			}
		}
	}

	private Path locateTestFile(Path dir, Class<?> clazz) throws IOException {
		String pkg = clazz.getPackage().getName();
		String className = clazz.getSimpleName() + "InVitroTest";
		Path testpackage = dir.resolve(pkg.replace('.', '/'));

		Files.createDirectories(testpackage);

		return testpackage.resolve(className + ".java");
	}

	public String renderTest(Class<?> clazz) {
		Set<String> localtests = tests.getOrDefault(clazz, emptySet());

		ST file = new ST(TEST_FILE);
		file.add("package", clazz.getPackage().getName());
		file.add("imports", imports.getImports());
		file.add("className", clazz.getSimpleName() + "InVitroTest");
		file.add("methods", localtests);

		return file.render();
	}

	private class MethodGenerator {

		private LocalVariableNameGenerator locals;

		private GeneratedSnapshot snapshot;
		private int no;

		private List<String> statements;

		private String base;
		private List<String> args;
		private String result;

		public MethodGenerator(GeneratedSnapshot snapshot, int no) {
			this.snapshot = snapshot;
			this.no = no;
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public MethodGenerator generateArrange() {
			statements.add(BEGIN_ARRANGE);

			ObjectToSetupCode setupCode = new ObjectToSetupCode(locals, imports);
			Computation setupThis = snapshot.getSetupThis().accept(setupCode);
			List<Computation> setupArgs = Stream.of(snapshot.getSetupArgs())
				.map(arg -> arg.accept(setupCode))
				.collect(toList());

			statements.addAll(setupThis.getStatements());
			statements.addAll(setupArgs.stream()
				.flatMap(arg -> arg.getStatements().stream())
				.collect(toList()));

			this.base = setupThis.isStored() 
				? setupThis.getValue()
				: assign(snapshot.getSetupThis().getType(), setupThis.getValue());
			this.args = IntStream.range(0, setupArgs.size())
				.mapToObj(i -> setupArgs.get(i).isStored() 
					? setupArgs.get(i).getValue()
					: assign(snapshot.getSetupArgs()[i].getType(), setupArgs.get(i).getValue()))
				.collect(toList());
			return this;
		}

		public MethodGenerator generateAct() {
			statements.add(BEGIN_ACT);

			Type resultType = snapshot.getResultType();
			String methodName = snapshot.getMethodName();

			ST call = new ST(CALL_EXPRESSION);
			call.add("base", base);
			call.add("method", methodName);
			call.add("args", args);

			if (resultType != void.class) {
				result = assign(resultType, call.render(), true);
			} else {
				execute(call.render());
			}

			return this;
		}

		public MethodGenerator generateAssert() {
			statements.add(BEGIN_ASSERT);

			ObjectToMatcherCode expectCode = new ObjectToMatcherCode(locals, imports);

			List<String> expectResult = Optional.ofNullable(snapshot.getExpectResult())
				.map(o -> expectCode.createAssertion(o, result))
				.orElse(emptyList());

			List<String> expectThis = Optional.of(snapshot.getExpectThis())
				.filter(o -> !o.equals(snapshot.getSetupThis()))
				.map(o -> expectCode.createAssertion(o, base))
				.orElse(emptyList());

			Type[] argumentTypes = snapshot.getArgumentTypes();
			SerializedValue[] serializedArgs = snapshot.getExpectArgs();
			List<String> expectArgs = IntStream.range(0, argumentTypes.length)
				.filter(i -> !isImmutable(argumentTypes[i]))
				.filter(i -> !serializedArgs[i].equals(snapshot.getSetupArgs()[i]))
				.mapToObj(i -> expectCode.createAssertion(serializedArgs[i], args.get(i)))
				.flatMap(statements -> statements.stream())
				.collect(toList());

			statements.addAll(expectResult);
			statements.addAll(expectThis);
			statements.addAll(expectArgs);

			return this;
		}

		public String assign(Type type, String value) {
			return assign(type, value, false);
		}

		public String assign(Type type, String value, boolean force) {
			if (isImmutable(type) && !force) {
				return value;
			} else {
				String name = locals.fetchName(type);

				ST assign = new ST(ASSIGN_STMT);
				assign.add("type", getSimpleName(type));
				assign.add("name", name);
				assign.add("value", value);

				statements.add(assign.render());

				return name;
			}
		}

		public void execute(String value) {
			ST statement = new ST(EXPRESSION_STMT);
			statement.add("value", value);

			statements.add(statement.render());
		}

		private boolean isImmutable(Type type) {
			return isPrimitive(type)
				|| IMMUTABLE_TYPES.contains(type);
		}

		public String generateTest() {
			ST test = new ST(TEST_TEMPLATE);
			test.add("testName", testName());
			test.add("statements", statements);
			return test.render();
		}

		private String testName() {
			String testName = snapshot.getMethodName();

			return toUpperCase(testName.charAt(0)) + testName.substring(1) + no;
		}

	}
}
