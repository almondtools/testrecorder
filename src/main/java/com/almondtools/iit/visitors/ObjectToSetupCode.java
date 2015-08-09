package com.almondtools.iit.visitors;

import static com.almondtools.iit.runtime.TemplateHelper.asLiteral;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.stringtemplate.v4.ST;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.SerializedValueVisitor;
import com.almondtools.iit.runtime.GenericObject;
import com.almondtools.iit.values.SerializedArray;
import com.almondtools.iit.values.SerializedField;
import com.almondtools.iit.values.SerializedList;
import com.almondtools.iit.values.SerializedLiteral;
import com.almondtools.iit.values.SerializedMap;
import com.almondtools.iit.values.SerializedNull;
import com.almondtools.iit.values.SerializedObject;
import com.almondtools.iit.values.SerializedSet;

public class ObjectToSetupCode implements SerializedValueVisitor<Computation> {

	private static final String GENERIC_OBJECT = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.as(<type>.class)";
	private static final String FIELD = "<type> <name> = <value>;";
	private static final String ARRAY_LITERAL = "new <type>{<elements; separator=\", \">}";

	private static final String ASSIGN_STMT = "<type> <name> = <value>;";
	private static final String CALL_PROC_STMT = "<base>.<method>(<arguments; separator=\", \">);";
	
	private LocalVariableNameGenerator locals;
	private Map<SerializedValue, String> computed;

	private Set<String> imports;

	public ObjectToSetupCode() {
		this(new LocalVariableNameGenerator());
	}
	
	public ObjectToSetupCode(LocalVariableNameGenerator locals) {
		this.locals = locals;
		this.computed = new IdentityHashMap<>();
		this.imports = new LinkedHashSet<>();
	}
	
	public LocalVariableNameGenerator getLocals() {
		return locals;
	}
	
	public Set<String> getImports() {
		return imports;
	}

	@Override
	public Computation visitField(SerializedField field) {
		if (!field.getType().isArray() && !field.getType().isPrimitive()) {
			imports.add(field.getType().getName());
		}
		
		Computation valueTemplate = field.getValue().accept(this);
		
		List<String> statements = valueTemplate.getStatements();
		
		ST statement = new ST(FIELD);
		statement.add("type", field.getType().getSimpleName());
		statement.add("name", field.getName());
		statement.add("value", valueTemplate.getValue());
		return new Computation(statement.render(), statements);
	}

	private void registerImports(Class<?>... types) {
		for (Class<?> type : types) {
			imports.add(type.getName());
		}
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		registerImports(value.getType(), GenericObject.class);
		
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
		statement.add("type", value.getType().getSimpleName());
		statement.add("fields", elements);
		return new Computation(statement.render(), statements);
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		registerImports(List.class, ArrayList.class);
		
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
		init.add("type", List.class.getSimpleName());
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
		
		return new Computation(name, statements);
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		registerImports(Set.class, LinkedHashSet.class);

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
		init.add("type", Set.class.getSimpleName());
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
		
		return new Computation(name, statements);
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		registerImports(Map.class);
		registerImports(LinkedHashMap.class);
		
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
		init.add("type", Map.class.getSimpleName());
		init.add("name", name);
		init.add("value", "new LinkedHashMap<>()");
		statements.add(init.render());
		
		for (Map.Entry<String,String> element : elements.entrySet()) {
			ST add = new ST(CALL_PROC_STMT);
			add.add("base", name);
			add.add("method", "put");
			add.add("arguments", asList(element.getKey(), element.getValue()));
			statements.add(add.render());
		}
		
		return new Computation(name, statements);
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		imports.add(value.getType().getComponentType().getName());
		
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
		statement.add("type", value.getType().getSimpleName());
		statement.add("elements", elements);
		return new Computation(statement.render(), statements);
	}

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		Object literalValue = value.getValue();
		if (literalValue instanceof String) {
			return new Computation(asLiteral((String) literalValue));
		} else {
			return new Computation(value.toString());
		}
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		return new Computation("null");
	}

}
