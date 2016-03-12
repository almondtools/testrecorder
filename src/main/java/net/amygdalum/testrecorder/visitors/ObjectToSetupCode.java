package net.amygdalum.testrecorder.visitors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.amygdalum.testrecorder.visitors.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.visitors.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.visitors.Templates.cast;
import static net.amygdalum.testrecorder.visitors.Templates.genericObjectConverter;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;
import static net.amygdalum.testrecorder.visitors.TypeManager.isHidden;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedCollectionVisitor;
import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.values.SerializedBigInteger;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class ObjectToSetupCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

	private LocalVariableNameGenerator locals;
	private Map<SerializedValue, String> computed;

	private TypeManager types;

	public ObjectToSetupCode() {
		this(new LocalVariableNameGenerator(), new TypeManager());
	}

	public ObjectToSetupCode(LocalVariableNameGenerator locals, TypeManager types) {
		this.locals = locals;
		this.types = types;
		this.computed = new IdentityHashMap<>();
	}

	public LocalVariableNameGenerator getLocals() {
		return locals;
	}

	public TypeManager getTypes() {
		return types;
	}

	private String localVariable(SerializedValue value, Type type) {
		String name = locals.fetchName(type);
		computed.put(value, name);
		return name;
	}

	@Override
	public Computation visitField(SerializedField field) {
		types.registerType(field.getType());

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		if (isHidden(field.getValue().getValueType()) && !isHidden(field.getType())) {
			String unwrapped = Templates.callMethod(valueTemplate.getValue(), "value");
			String casted = cast(types.getSimpleName(field.getType()), unwrapped);

			String assignField = assignLocalVariableStatement(types.getSimpleName(field.getType()), field.getName(), casted);
			return new Computation(assignField, statements);
		} else {
			String assignField = assignLocalVariableStatement(types.getSimpleName(field.getType()), field.getName(), valueTemplate.getValue());
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

	private Computation renderBeanSetup(SerializedObject value) throws BeanSetupFailedException {
		try {
			String name = localVariable(value, value.getValueType());
			return new Construction(name, value).computeBest(types, this);
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new BeanSetupFailedException();
		}
	}

	private Computation renderGenericSetup(SerializedObject value) {
		types.registerTypes(value.getType(), GenericObject.class);

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

		String genericObject = genericObjectConverter(types.getRawTypeName(value.getValueType()), elements);

		String name = localVariable(value, value.getValueType());
		statements.add(assignLocalVariableStatement(types.getRawName(value.getValueType()), name, genericObject));

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
		types.registerTypes(value.getType(), value.getValueType());

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

		String list = newObject(types.getBestName(value.getValueType()));
		String listInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), name, list);
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
		types.registerTypes(value.getType(), value.getValueType());

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

		String set = newObject(types.getBestName(value.getValueType()));
		String setInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), name, set);
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
		types.registerTypes(value.getType(), value.getValueType());

		Map<Computation, Computation> elementTemplates = value.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().accept(this), entry -> entry.getValue().accept(this)));

		Map<String, String> elements = elementTemplates.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()));

		List<String> statements = elementTemplates.entrySet().stream()
			.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
			.distinct()
			.collect(toList());

		String name = localVariable(value, Map.class);

		String map = newObject(types.getBestName(value.getValueType()));
		String mapInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), name, map);
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
		types.registerType(value.getType());

		List<Computation> elementTemplates = Stream.of(value.getArray())
			.map(element -> element.accept(this))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String arrayLiteral = arrayLiteral(types.getSimpleName(value.getType()), elements);

		String name = localVariable(value, value.getType());
		statements.add(assignLocalVariableStatement(types.getSimpleName(value.getType()), name, arrayLiteral));

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
		types.registerImport(BigDecimal.class);

		String literal = asLiteral(value.getValue().toPlainString());
		String bigDecimal = newObject("BigDecimal", literal);
		return new Computation(bigDecimal);
	}

	@Override
	public Computation visitBigInteger(SerializedBigInteger value) {
		types.registerImport(BigInteger.class);

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
		public ObjectToSetupCode create(LocalVariableNameGenerator locals, TypeManager types) {
			return new ObjectToSetupCode(locals, types);
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
