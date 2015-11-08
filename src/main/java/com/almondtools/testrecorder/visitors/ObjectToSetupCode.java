package com.almondtools.testrecorder.visitors;

import static com.almondtools.testrecorder.TemplateHelper.asLiteral;
import static com.almondtools.testrecorder.TypeHelper.getSimpleName;
import static com.almondtools.testrecorder.visitors.Templates.arrayLiteral;
import static com.almondtools.testrecorder.visitors.Templates.assignField;
import static com.almondtools.testrecorder.visitors.Templates.assignStatement;
import static com.almondtools.testrecorder.visitors.Templates.callMethodStatement;
import static com.almondtools.testrecorder.visitors.Templates.genericObjectConverter;
import static com.almondtools.testrecorder.visitors.Templates.newObject;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

import com.almondtools.testrecorder.GenericObject;
import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.SerializedImmutableVisitor;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
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

	@Override
	public Computation visitField(SerializedField field) {
		imports.registerImport(field.getType());

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		String assignField = assignField(getSimpleName(field.getType()), field.getName(), valueTemplate.getValue());
		return new Computation(assignField, statements);
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		try {
			return renderBeanSetup(value);
		} catch (IntrospectionException | ReflectiveOperationException | RuntimeException | BeanSetupFailedException e) {
			return renderGenericSetup(value);
		}
	}

	private Computation renderBeanSetup(SerializedObject value) throws IntrospectionException, ReflectiveOperationException, BeanSetupFailedException {
		try {
			return renderConstructorBean(value);
		} catch (BeanSetupFailedException e) {
			return renderClassicBean(value);
		}
	}

	private Computation renderConstructorBean(SerializedObject value) throws IntrospectionException, ReflectiveOperationException, BeanSetupFailedException {
		Class<?> clazz = value.getObjectType();

		List<Type> requiredParameterTypes = value.getFields().stream()
			.map(field -> field.getType())
			.collect(toList());

		Constructor<?> constructor = findConstructor(clazz, requiredParameterTypes);
		if (constructor == null) {
			throw new BeanSetupFailedException();
		} else {
			List<SerializedField> orderedFields = orderFields(constructor.getGenericParameterTypes(), value.getFields());
			if (constructSuccessful(constructor, orderedFields)) {
				List<String> statements = new ArrayList<>();

				String name = locals.fetchName(clazz);

				List<Computation> computations = orderedFields.stream()
					.map(field -> field.getValue())
					.map(fieldValue -> fieldValue.accept(this))
					.collect(toList());
				
				statements.addAll(computations.stream()
					.flatMap(computation -> computation.getStatements().stream())
					.collect(toList()));
				
				String[] params = computations.stream()
					.map(computation -> computation.getValue())
					.toArray(len -> new String[len]);
				
				String bean = newObject(getSimpleName(clazz), params);
				String constructorStatement = assignStatement(getSimpleName(clazz), name, bean);
				statements.add(constructorStatement);

				return new Computation(name, true, statements);
			} else {
				throw new BeanSetupFailedException();
			}
		}

	}

	private boolean constructSuccessful(Constructor<?> constructor, List<SerializedField> fields) {
		Deserializer deserializer = new Deserializer();
		Object[] arguments = fields.stream()
			.map(field -> field.getValue().accept(deserializer))
			.toArray(len -> new Object[len]);
		try {
			Object o = constructor.newInstance(arguments);
			for (int i = 0; i < arguments.length; i++) {
				SerializedField field = fields.get(i);
				if (!isSet(o, field.getName(), arguments[i])) {
					return false;
				}
			}
			return true;
		} catch (ReflectiveOperationException e) {
			return false;
		}
	}

	private Constructor<?> findConstructor(Class<?> clazz, List<Type> requiredParameterTypes) throws BeanSetupFailedException {
		for (Constructor<?> constructor : clazz.getConstructors()) {
			List<Type> foundTypes = asList(constructor.getGenericParameterTypes());
			if (requiredParameterTypes.containsAll(foundTypes)
				&& foundTypes.containsAll(requiredParameterTypes)) {
				return constructor;
			}
		}
		throw new BeanSetupFailedException();
	}

	private List<SerializedField> orderFields(Type[] types, List<SerializedField> fields) throws BeanSetupFailedException {
		List<SerializedField> ordered = new ArrayList<>();
		for (Type type : types) {
			SerializedField matchingField = fields.stream()
				.filter(field -> type.equals(field.getType()))
				.findFirst()
				.orElseThrow(() -> new BeanSetupFailedException());
			ordered.add(matchingField);
		}
		return ordered;
	}

	private Computation renderClassicBean(SerializedObject value) throws IntrospectionException, ReflectiveOperationException, BeanSetupFailedException {
		Class<?> clazz = value.getObjectType();

		BeanInfo info = Introspector.getBeanInfo(clazz);
		Set<String> requiredProperties = value.getFields().stream()
			.map(field -> field.getName())
			.collect(toSet());
		Map<String, PropertyDescriptor> writeableProperties = writeableProperties(info, requiredProperties);
		if (writeableProperties.size() != requiredProperties.size()) {
			throw new BeanSetupFailedException();
		}
		List<String> statements = new ArrayList<>();

		String name = locals.fetchName(clazz);

		String bean = newObject(getSimpleName(clazz));
		String constructorStatement = assignStatement(getSimpleName(clazz), name, bean);
		statements.add(constructorStatement);

		Object o = clazz.newInstance();
		for (SerializedField field : value.getFields()) {
			String fieldName = field.getName();
			PropertyDescriptor setProperty = writeableProperties.get(fieldName);
			SerializedValue fieldvalue = field.getValue();

			if (setSuccessful(o, setProperty, fieldvalue)) {
				Computation fieldComputation = fieldvalue.accept(this);
				statements.addAll(fieldComputation.getStatements());

				String setStatement = callMethodStatement(name, setProperty.getWriteMethod().getName(), fieldComputation.getValue());
				statements.add(setStatement);
			} else {
				throw new BeanSetupFailedException();
			}
		}

		return new Computation(name, true, statements);
	}

	private boolean setSuccessful(Object base, PropertyDescriptor setProperty, SerializedValue fieldvalue) throws BeanSetupFailedException, ReflectiveOperationException {
		String fieldName = setProperty.getName();

		Object setValue = fieldvalue.accept(new Deserializer());

		setProperty.getWriteMethod().invoke(base, setValue);
		return isSet(base, fieldName, setValue);
	}

	private boolean isSet(Object base, String fieldName, Object expectedValue) throws IllegalAccessException {
		Class<?> clazz = base.getClass();
		while (clazz != Object.class) {
			try {
				Field f = clazz.getDeclaredField(fieldName);
				boolean accessible = f.isAccessible();
				try {
					if (!accessible) {
						f.setAccessible(true);
					}
					Object foundValue = f.get(base);
					if (foundValue == expectedValue) {
						return true;
					} else if (foundValue == null || expectedValue == null) {
						return false;
					} else {
						return foundValue.equals(expectedValue);
					}
				} finally {
					if (!accessible) {
						f.setAccessible(true);
					}
				}
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return false;
	}

	private Computation renderGenericSetup(SerializedObject value) {
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

		String genericObject = genericObjectConverter(getSimpleName(value.getObjectType()), elements);
		return new Computation(genericObject, statements);
	}

	private Map<String, PropertyDescriptor> writeableProperties(BeanInfo info, Set<String> required) {
		return Stream.of(info.getPropertyDescriptors())
			.filter(property -> property.getWriteMethod() != null)
			.filter(property -> required.contains(property.getName()))
			.collect(toMap(property -> property.getName(), property -> property));
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return renderListSetup(value);
	}

	private Computation renderListSetup(SerializedList value) {
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

		String listInit = assignStatement(getSimpleName(value.getType()), name, "new ArrayList<>()");
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

		String setInit = assignStatement(getSimpleName(value.getType()), name, "new LinkedHashSet<>()");
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

		String mapInit = assignStatement(getSimpleName(value.getType()), name, "new LinkedHashMap<>()");
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
		return new Computation(arrayLiteral, statements);
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

	}

	private static class BeanSetupFailedException extends Exception {
		public BeanSetupFailedException() {
		}
	}
}
