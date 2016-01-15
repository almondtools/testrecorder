package com.almondtools.testrecorder.visitors;

import static com.almondtools.testrecorder.TypeHelper.getBestName;
import static com.almondtools.testrecorder.TypeHelper.getRawName;
import static com.almondtools.testrecorder.TypeHelper.getRawTypeName;
import static com.almondtools.testrecorder.TypeHelper.getSimpleName;
import static com.almondtools.testrecorder.TypeHelper.isHidden;
import static com.almondtools.testrecorder.visitors.Templates.arrayLiteral;
import static com.almondtools.testrecorder.visitors.Templates.asLiteral;
import static com.almondtools.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static com.almondtools.testrecorder.visitors.Templates.callMethodStatement;
import static com.almondtools.testrecorder.visitors.Templates.cast;
import static com.almondtools.testrecorder.visitors.Templates.genericObjectConverter;
import static com.almondtools.testrecorder.visitors.Templates.newObject;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.SerializedImmutableVisitor;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.util.GenericObject;
import com.almondtools.testrecorder.values.SerializedArray;
import com.almondtools.testrecorder.values.SerializedBigDecimal;
import com.almondtools.testrecorder.values.SerializedBigInteger;
import com.almondtools.testrecorder.values.SerializedField;
import com.almondtools.testrecorder.values.SerializedList;
import com.almondtools.testrecorder.values.SerializedLiteral;
import com.almondtools.testrecorder.values.SerializedMap;
import com.almondtools.testrecorder.values.SerializedNull;
import com.almondtools.testrecorder.values.SerializedObject;
import com.almondtools.testrecorder.values.SerializedSet;

public class ObjectToSetupCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

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

	private String localVariable(SerializedValue value, Type type) {
		String name = locals.fetchName(type);
		computed.put(value, name);
		return name;
	}

	@Override
	public Computation visitField(SerializedField field) {
		imports.registerImport(field.getType());

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		if (isHidden(field.getValue().getValueType()) && !isHidden(field.getType())) {
			String unwrapped = callMethodStatement(valueTemplate.getValue() ,"value");
			String casted = cast(getSimpleName(field.getType()), unwrapped);
			
			String assignField = assignLocalVariableStatement(getSimpleName(field.getType()), field.getName(), casted);
			return new Computation(assignField, statements);
		} else {
			String assignField = assignLocalVariableStatement(getSimpleName(field.getType()), field.getName(), valueTemplate.getValue());
			return new Computation(assignField, statements);
		}
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		try {
			return renderBeanSetup(value);
		} catch (BeanSetupFailedException e) {
			return renderGenericSetup(value);
		}
	}

	private Computation renderBeanSetup(SerializedObject value) throws BeanSetupFailedException  {
		try {
			String name = localVariable(value, value.getValueType());
			return new Construction(name, value).computeBest(this);
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new BeanSetupFailedException();
		}
	}

	private Computation renderGenericSetup(SerializedObject value) {
		Type[] types = { value.getType(), GenericObject.class };
		imports.registerImports(types);

		List<Computation> elementTemplates = value.getFields().stream()
			.sorted()
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String genericObject = genericObjectConverter(getRawTypeName(value.getValueType()), elements);
		
		String name = localVariable(value, value.getValueType());
		statements.add(assignLocalVariableStatement(getRawName(value.getValueType()), name, genericObject));
		
		return new Computation(name, statements);
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return renderListSetup(value);
	}

	private Computation renderListSetup(SerializedList value) {
		imports.registerImports(value.getType(), value.getValueType());

		List<Computation> elementTemplates = value.stream()
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String name = localVariable(value, List.class);

		String list = newObject(getBestName(value.getValueType()));
		String listInit = assignLocalVariableStatement(getSimpleName(value.getType()), name, list);
		statements.add(listInit);

		for (String element : elements) {
			String addElement = callMethodStatement(name, "add", element);
			statements.add(addElement);
		}

		return new Computation(name, true, statements);
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return renderSetSetup(value);
	}

	private Computation renderSetSetup(SerializedSet value) {
		imports.registerImports(value.getType(), value.getValueType());

		List<Computation> elementTemplates = value.stream()
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String name = localVariable(value, Set.class);

		String set = newObject(getBestName(value.getValueType()));
		String setInit = assignLocalVariableStatement(getSimpleName(value.getType()), name, set);
		statements.add(setInit);

		for (String element : elements) {
			String addElement = callMethodStatement(name, "add", element);
			statements.add(addElement);
		}

		return new Computation(name, true, statements);
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return renderMapSetup(value);
	}

	private Computation renderMapSetup(SerializedMap value) {
		imports.registerImports(value.getType(), value.getValueType());

		Map<Computation, Computation> elementTemplates = value.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().accept(this), entry -> entry.getValue().accept(this)));

		Map<String, String> elements = elementTemplates.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()));

		List<String> statements = elementTemplates.entrySet().stream()
			.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
			.distinct()
			.collect(toList());

		String name = localVariable(value, Map.class);

		String map = newObject(getBestName(value.getValueType()));
		String mapInit = assignLocalVariableStatement(getSimpleName(value.getType()), name, map);
		statements.add(mapInit);

		for (Map.Entry<String, String> element : elements.entrySet()) {
			String putEntry = callMethodStatement(name, "put", element.getKey(), element.getValue());
			statements.add(putEntry);
		}

		return new Computation(name, true, statements);
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return renderArraySetup(value);
	}

	private Computation renderArraySetup(SerializedArray value) {
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

		String arrayLiteral = arrayLiteral(getSimpleName(value.getType()), elements);

		String name = localVariable(value, value.getType());
		statements.add(assignLocalVariableStatement(getSimpleName(value.getType()), name, arrayLiteral));

		return new Computation(name, statements);
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
		String bigDecimal = newObject("BigDecimal", literal);
		return new Computation(bigDecimal);
	}

	@Override
	public Computation visitBigInteger(SerializedBigInteger value) {
		imports.registerImport(BigInteger.class);

		String literal = asLiteral(value.getValue().toString());
		String bigInteger = newObject("BigInteger", literal);
		return new Computation(bigInteger);
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		return new Computation("null");
	}

	@Override
	public Computation visitUnknown(SerializedValue value) {
		return Computation.NULL;
	}

	public static class Factory implements SerializedValueVisitorFactory {

		@Override
		public ObjectToSetupCode create(LocalVariableNameGenerator locals, ImportManager imports) {
			return new ObjectToSetupCode(locals, imports);
		}

		@Override
		public Type resultType(Type type) {
			return type;
		}
	}

	private static class BeanSetupFailedException extends Exception {
		public BeanSetupFailedException() {
		}
	}
}
