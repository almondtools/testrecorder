package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.SnapshotInstrumentor.SNAPSHOT_GENERATOR_FIELD_NAME;
import static com.almondtools.testrecorder.generator.TypeHelper.getBase;
import static com.almondtools.testrecorder.generator.TypeHelper.getSimpleName;
import static com.almondtools.testrecorder.visitors.Templates.returnStatement;
import static java.util.Collections.synchronizedMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;

import com.almondtools.testrecorder.generator.ValueSnapshotConsumer;
import com.almondtools.testrecorder.visitors.Computation;
import com.almondtools.testrecorder.visitors.ImportManager;
import com.almondtools.testrecorder.visitors.LocalVariableNameGenerator;
import com.almondtools.testrecorder.visitors.ObjectToSetupCode;
import com.almondtools.testrecorder.visitors.SerializedValueVisitorFactory;

public class ValueGenerator implements ValueSnapshotConsumer {

	private static final String TEST_FILE = "package <package>;\n\n"
		+ "<imports: {pkg | import <pkg>;\n}>"
		+ "\n\n\n"
		+ "public class <className> {\n"
		+ "\n"
		+ "  <methods; separator=\"\\n\">"
		+ "\n}";

	private static final String CODE_TEMPLATE = "\n"
		+ "public <resultType> <valueName>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";

	private static final String VALUES = "Values";

	private ImportManager imports;
	private SerializedValueVisitorFactory serializers;
	private Map<Class<?>, List<String>> values;

	public ValueGenerator() {
		this.imports = new ImportManager();
		this.serializers = new ObjectToSetupCode.Factory();
		this.values = synchronizedMap(new LinkedHashMap<>());
	}
	
	@Override
	public void accept(ValueSnapshot snapshot) {
		List<String> localvalues= values.computeIfAbsent(getBase(snapshot.getDeclaringClass()), key -> new ArrayList<>());

		CodeGenerator methodGenerator = new CodeGenerator(snapshot, localvalues.size())
			.generateValue();

		localvalues.add(methodGenerator.generateCode());
	}

	public List<String> valuesFor(Class<?> clazz) {
		return values.computeIfAbsent(getBase(clazz), key -> new ArrayList<>());
	}

	public String renderCode(Class<?> clazz) {
		List<String> localvalues = valuesFor(clazz);

		ST file = new ST(TEST_FILE);
		file.add("package", clazz.getPackage().getName());
		file.add("imports", imports.getImports());
		file.add("className", computeClassName(clazz));
		file.add("methods", localvalues);

		return file.render();
	}

	public String computeClassName(Class<?> clazz) {
		return clazz.getSimpleName() + VALUES;
	}

	private class CodeGenerator {

		private LocalVariableNameGenerator locals;

		private ValueSnapshot snapshot;
		private int no;

		private String resultType;
		private List<String> statements;

		public CodeGenerator(ValueSnapshot snapshot, int no) {
			this.snapshot = snapshot;
			this.resultType = getSimpleName(snapshot.getValue().getType());
			this.no = no;
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public CodeGenerator generateValue() {
			SerializedValueVisitor<Computation> serializer = serializers.create(locals, imports);
			
			Computation serialized = snapshot.getValue().accept(serializer);

			statements.addAll(serialized.getStatements());
			statements.add(returnStatement(serialized.getValue()));
			return this;
		}

		public String generateCode() {
			ST code = new ST(CODE_TEMPLATE);
			code.add("resultType", resultType);
			code.add("valueName", valueName());
			code.add("statements", statements);
			return code.render();
		}

		public String valueName() {
			return snapshot.getFieldName() + no;
		}

	}

	public static ValueGenerator fromRecorded(Object object) {
		Class<? extends Object> clazz = object.getClass();
		try {
			Field field = clazz.getDeclaredField(SNAPSHOT_GENERATOR_FIELD_NAME);
			field.setAccessible(true);
			SnapshotGenerator generator = (SnapshotGenerator) field.get(object);
			return (ValueGenerator) generator.getValueConsumer();
		} catch (RuntimeException | ReflectiveOperationException e) {
			return null;
		}
	}

}
