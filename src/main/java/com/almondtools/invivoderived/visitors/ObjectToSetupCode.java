package com.almondtools.invivoderived.visitors;

import static com.almondtools.invivoderived.generator.TemplateHelper.asLiteral;
import static com.almondtools.invivoderived.generator.TypeHelper.getSimpleName;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.stringtemplate.v4.ST;

import com.almondtools.invivoderived.SerializedCollectionVisitor;
import com.almondtools.invivoderived.SerializedImmutableVisitor;
import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.SerializedValueVisitor;
import com.almondtools.invivoderived.generator.GenericObject;
import com.almondtools.invivoderived.generator.TypeHelper;
import com.almondtools.invivoderived.values.SerializedArray;
import com.almondtools.invivoderived.values.SerializedBigDecimal;
import com.almondtools.invivoderived.values.SerializedBigInteger;
import com.almondtools.invivoderived.values.SerializedField;
import com.almondtools.invivoderived.values.SerializedList;
import com.almondtools.invivoderived.values.SerializedLiteral;
import com.almondtools.invivoderived.values.SerializedMap;
import com.almondtools.invivoderived.values.SerializedNull;
import com.almondtools.invivoderived.values.SerializedObject;
import com.almondtools.invivoderived.values.SerializedSet;

public class ObjectToSetupCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

	private static final String GENERIC_OBJECT = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.as(<type>.class)";
	private static final String FIELD = "<type> <name> = <value>;";
	private static final String ARRAY_LITERAL = "new <type>{<elements; separator=\", \">}";
	private static final String NEW_OBJECT = "new <type>(<args; separator=\", \">)";

	private static final String ASSIGN_STMT = "<type> <name> = <value>;";
	private static final String CALL_PROC_STMT = "<base>.<method>(<arguments; separator=\", \">);";

	private LocalVariableNameGenerator locals;
	private Map<SerializedValue, String> computed;

	private ImportManager imports;

	public ObjectToSetupCode() {
		this(new LocalVariableNameGenerator(), new ImportManager());
	}

	public ObjectToSetupCode(LocalVariableNameGenerator locals, ImportManager imports) {
		this.locals = locals;
		this.imports = imports;
		this.computed = new IdentityHashMap<>();
	}

	public LocalVariableNameGenerator getLocals() {
		return locals;
	}

	public ImportManager getImports() {
		return imports;
	}

	@Override
	public Computation visitField(SerializedField field) {
		imports.registerImport(field.getType());

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		ST statement = new ST(FIELD);
		statement.add("type", TypeHelper.getSimpleName(field.getType()));
		statement.add("name", field.getName());
		statement.add("value", valueTemplate.getValue());
		return new Computation(statement.render(), statements);
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		Type[] types = { value.getType(), GenericObject.class };
		imports.registerImports(types);

		List<Computation> elementTemplates = value.getFields().stream()
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		ST statement = new ST(GENERIC_OBJECT);
		statement.add("type", getSimpleName(value.getType()));
		statement.add("fields", elements);
		return new Computation(statement.render(), statements);
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		imports.registerImports(value.getType(), ArrayList.class);

		List<Computation> elementTemplates = value.stream()
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String name = locals.fetchName(List.class);

		ST init = new ST(ASSIGN_STMT);
		init.add("type", TypeHelper.getSimpleName(value.getType()));
		init.add("name", name);
		init.add("value", "new ArrayList<>()");
		statements.add(init.render());

		for (String element : elements) {
			ST add = new ST(CALL_PROC_STMT);
			add.add("base", name);
			add.add("method", "add");
			add.add("arguments", asList(element));
			statements.add(add.render());
		}

		return new Computation(name, true, statements);
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		imports.registerImports(value.getType(), LinkedHashSet.class);

		List<Computation> elementTemplates = value.stream()
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String name = locals.fetchName(Set.class);

		ST init = new ST(ASSIGN_STMT);
		init.add("type", TypeHelper.getSimpleName(value.getType()));
		init.add("name", name);
		init.add("value", "new LinkedHashSet<>()");
		statements.add(init.render());

		for (String element : elements) {
			ST add = new ST(CALL_PROC_STMT);
			add.add("base", name);
			add.add("method", "add");
			add.add("arguments", asList(element));
			statements.add(add.render());
		}

		return new Computation(name, true, statements);
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		imports.registerImports(value.getType(), LinkedHashMap.class);

		Map<Computation, Computation> elementTemplates = value.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().accept(this), entry -> entry.getValue().accept(this)));

		Map<String, String> elements = elementTemplates.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()));

		List<String> statements = elementTemplates.entrySet().stream()
			.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
			.distinct()
			.collect(toList());

		String name = locals.fetchName(Map.class);

		ST init = new ST(ASSIGN_STMT);
		init.add("type", TypeHelper.getSimpleName(value.getType()));
		init.add("name", name);
		init.add("value", "new LinkedHashMap<>()");
		statements.add(init.render());

		for (Map.Entry<String, String> element : elements.entrySet()) {
			ST add = new ST(CALL_PROC_STMT);
			add.add("base", name);
			add.add("method", "put");
			add.add("arguments", asList(element.getKey(), element.getValue()));
			statements.add(add.render());
		}

		return new Computation(name, true, statements);
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		imports.registerImport(value.getType());

		List<Computation> elementTemplates = Stream.of(value.getArray())
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		ST statement = new ST(ARRAY_LITERAL);
		statement.add("type", getSimpleName(value.getType()));
		statement.add("elements", elements);
		return new Computation(statement.render(), statements);
	}

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		Object literalValue = value.getValue();
		String literal = asLiteral(literalValue);
		return new Computation(literal);
	}

	@Override
	public Computation visitBigDecimal(SerializedBigDecimal value) {
		imports.registerImport(BigDecimal.class);

		String literal = asLiteral(value.getValue().toPlainString());
		ST expression = new ST(NEW_OBJECT);
		expression.add("type", "BigDecimal");
		expression.add("args", literal);
		return new Computation(expression.render());
	}

	@Override
	public Computation visitBigInteger(SerializedBigInteger value) {
		imports.registerImport(BigInteger.class);

		String literal = asLiteral(value.getValue().toString());
		ST expression = new ST(NEW_OBJECT);
		expression.add("type", "BigInteger");
		expression.add("args", literal);
		return new Computation(expression.render());
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		return new Computation("null");
	}

	@Override
	public Computation visitUnknown(SerializedValue value) {
		return Computation.NULL;
	}

}
