package com.almondtools.invivoderived.generator;

import static java.lang.Character.toUpperCase;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.stringtemplate.v4.ST;

import com.almondtools.invivoderived.GeneratedSnapshot;
import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.visitors.Computation;
import com.almondtools.invivoderived.visitors.LocalVariableNameGenerator;
import com.almondtools.invivoderived.visitors.ObjectToMatcherCode;
import com.almondtools.invivoderived.visitors.ObjectToSetupCode;

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

	private Set<String> tests;
	private Set<String> imports;

	public TestGenerator() {
		this.tests = new LinkedHashSet<>();
		this.imports = new LinkedHashSet<>();
		imports.add(Test.class.getName());
	}

	@Override
	public void accept(GeneratedSnapshot snapshot) {
		MethodGenerator methodGenerator = new MethodGenerator(snapshot, tests.size())
			.generateArrange()
			.generateAct()
			.generateAssert();
		tests.add(methodGenerator.generateTest());
		imports.addAll(methodGenerator.getImports());
	}

	public void writeTests(Path dir, Class<?> clazz) {
		String pkg = clazz.getPackage().getName();
		String className = clazz.getSimpleName() + "InVitroTest";

		ST file = new ST(TEST_FILE);
		file.add("package", pkg);
		file.add("imports", imports);
		file.add("className", className);
		file.add("methods", tests);

		Path testpackage = dir.resolve(pkg.replace('.', '/'));
		try {
			Files.createDirectories(testpackage);
			Path testfile = testpackage.resolve(className + ".java");
			try (Writer writer = Files.newBufferedWriter(testfile)) {
				writer.write(file.render());
			}
		} catch (IOException e) {
			System.out.println(file.render());
		}
	}

	private static class MethodGenerator {

		private LocalVariableNameGenerator locals;
		private GeneratedSnapshot snapshot;
		private int no;

		private Set<String> imports;
		private List<String> statements;

		private String base;
		private List<String> args;
		private String result;

		public MethodGenerator(GeneratedSnapshot snapshot, int no) {
			this.snapshot = snapshot;
			this.no = no;
			this.locals = new LocalVariableNameGenerator();
			this.imports = new LinkedHashSet<>();
			this.statements = new ArrayList<>();
		}

		public Set<String> getImports() {
			return imports;
		}

		public MethodGenerator generateArrange() {
			statements.add(BEGIN_ARRANGE);

			ObjectToSetupCode setupCode = new ObjectToSetupCode(locals);
			Computation setupThis = snapshot.getSetupThis().accept(setupCode);
			List<Computation> setupArgs = Stream.of(snapshot.getSetupArgs())
				.map(arg -> arg.accept(setupCode))
				.collect(toList());

			imports.addAll(setupCode.getImports());

			statements.addAll(setupThis.getStatements());
			statements.addAll(setupArgs.stream()
				.flatMap(arg -> arg.getStatements().stream())
				.collect(toList()));

			this.base = assign(snapshot.getSetupThis().getType(), setupThis.getValue());
			this.args = IntStream.range(0, setupArgs.size())
				.mapToObj(i -> assign(snapshot.getSetupArgs()[i].getType(), setupArgs.get(i).getValue()))
				.collect(toList());
			return this;
		}

		public MethodGenerator generateAct() {
			statements.add(BEGIN_ACT);

			Class<?> resultType = snapshot.getResultType();
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

			ObjectToMatcherCode expectCode = new ObjectToMatcherCode(locals);

			List<String> expectResult = Optional.ofNullable(snapshot.getExpectResult())
				.map(o -> expectCode.createAssertion(o, result))
				.orElse(emptyList());

			List<String> expectThis = Optional.of(snapshot.getExpectThis())
				.filter(o -> !o.equals(snapshot.getSetupThis()))
				.map(o -> expectCode.createAssertion(o, base))
				.orElse(emptyList());

			Class<?>[] argumentTypes = snapshot.getArgumentTypes();
			SerializedValue[] serializedArgs = snapshot.getExpectArgs();
			List<String> expectArgs = IntStream.range(0, argumentTypes.length)
				.filter(i -> !isImmutable(argumentTypes[i]))
				.filter(i -> !serializedArgs[i].equals(snapshot.getSetupArgs()[i]))
				.mapToObj(i -> expectCode.createAssertion(serializedArgs[i], args.get(i)))
				.flatMap(statements -> statements.stream())
				.collect(toList());

			imports.addAll(expectCode.getImports());

			statements.addAll(expectResult);
			statements.addAll(expectThis);
			statements.addAll(expectArgs);

			return this;
		}

		public String assign(Class<?> type, String value) {
			return assign(type, value, false);
		}

		public String assign(Class<?> type, String value, boolean force) {
			if (isImmutable(type) && !force) {
				return value;
			} else {
				String name = locals.fetchName(type);

				ST assign = new ST(ASSIGN_STMT);
				assign.add("type", type.getSimpleName());
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

		private boolean isImmutable(Class<?> clazz) {
			return clazz.isPrimitive()
				|| IMMUTABLE_TYPES.contains(clazz);
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
