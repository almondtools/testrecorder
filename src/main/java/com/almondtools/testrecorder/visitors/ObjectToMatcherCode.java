package com.almondtools.testrecorder.visitors;

import static com.almondtools.testrecorder.TypeHelper.getRawName;
import static com.almondtools.testrecorder.TypeHelper.getSimpleName;
import static com.almondtools.testrecorder.TypeHelper.isPrimitive;
import static com.almondtools.testrecorder.TypeHelper.parameterized;
import static com.almondtools.testrecorder.TypeHelper.wildcard;
import static com.almondtools.testrecorder.util.TemplateHelper.asLiteral;
import static com.almondtools.testrecorder.visitors.Templates.arrayContainingMatcher;
import static com.almondtools.testrecorder.visitors.Templates.assignField;
import static com.almondtools.testrecorder.visitors.Templates.containsEntriesMatcher;
import static com.almondtools.testrecorder.visitors.Templates.containsInAnyOrderMatcher;
import static com.almondtools.testrecorder.visitors.Templates.containsMatcher;
import static com.almondtools.testrecorder.visitors.Templates.emptyMatcher;
import static com.almondtools.testrecorder.visitors.Templates.equalToMatcher;
import static com.almondtools.testrecorder.visitors.Templates.genericObjectMatcher;
import static com.almondtools.testrecorder.visitors.Templates.newObject;
import static com.almondtools.testrecorder.visitors.Templates.noEntriesMatcher;
import static com.almondtools.testrecorder.visitors.Templates.nullMatcher;
import static com.almondtools.testrecorder.visitors.Templates.primitiveArrayContainingMatcher;
import static com.almondtools.testrecorder.visitors.Templates.recursiveMatcher;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.almondtools.conmatch.datatypes.MapMatcher;
import com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher;
import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.SerializedImmutableVisitor;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.util.GenericMatcher;
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

public class ObjectToMatcherCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

	private Set<SerializedValue> computed;

	private ImportManager imports;

	public ObjectToMatcherCode() {
		this(new LocalVariableNameGenerator(), new ImportManager());
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals, ImportManager imports) {
		this.imports = imports;
		this.computed = new HashSet<>();
	}
	
	public ImportManager getImports() {
		return imports;
	}

	@Override
	public Computation visitField(SerializedField field) {
		SerializedValue fieldValue = field.getValue();
		if (isSimpleValue(fieldValue)) {
			Computation value = getSimpleValue(fieldValue);

			String assignField = assignField(getSimpleName(field.getType()), field.getName(), value.getValue());
			return new Computation(assignField, value.getStatements());
		} else {
			imports.registerImport(Matcher.class);
			Computation value = fieldValue.accept(this);

			String genericType = getSimpleName(value.getType());

			String assignField = assignField(genericType, field.getName(), value.getValue());
			return new Computation(assignField, value.getStatements());
		}
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (!computed.add(value)) {
			imports.staticImport(GenericMatcher.class,"recursive");
			Type resultType = value.getType().equals(value.getValueType()) ? parameterized(Matcher.class, null, value.getType()) : parameterized(Matcher.class, null, wildcard());
			return new Computation(recursiveMatcher(getRawName(value.getValueType())), resultType);
		}
		Type[] types = { value.getType(), GenericMatcher.class };
		imports.registerImports(types);

		List<Computation> fields = value.getFields().stream()
			.sorted()
			.map(field -> field.accept(this))
			.collect(toList());

		List<String> fieldComputations = fields.stream()
			.flatMap(field -> field.getStatements().stream())
			.collect(toList());

		List<String> fieldAssignments = fields.stream()
			.map(field -> field.getValue())
			.collect(toList());

		String matcherRawType = getRawName(value.getValueType());
		String matcherExpression = genericObjectMatcher(matcherRawType, fieldAssignments);
		Type resultType = value.getType().equals(value.getValueType()) ? parameterized(Matcher.class, null, value.getType()) : parameterized(Matcher.class, null, wildcard());

		return new Computation(matcherExpression, resultType, fieldComputations);
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (!computed.add(value)) {
			imports.staticImport(GenericMatcher.class,"recursive");
			return new Computation(recursiveMatcher(getRawName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		if (value.isEmpty()) {
			imports.staticImport(Matchers.class, "empty");

			return new Computation(emptyMatcher(), parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			imports.staticImport(Matchers.class, "contains");

			List<Computation> elements = value.stream()
				.map(element -> getSimpleValue(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String containsMatcher = containsMatcher(elementValues);

			return new Computation(containsMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (!computed.add(value)) {
			imports.staticImport(GenericMatcher.class,"recursive");
			return new Computation(recursiveMatcher(getRawName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		if (value.isEmpty()) {
			imports.staticImport(Matchers.class, "empty");

			String emptyMatcher = emptyMatcher();
			return new Computation(emptyMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			imports.staticImport(Matchers.class, "containsInAnyOrder");

			List<Computation> elements = value.stream()
				.map(element -> getSimpleValue(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String containsInAnyOrderMatcher = containsInAnyOrderMatcher(elementValues);
			return new Computation(containsInAnyOrderMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (!computed.add(value)) {
			imports.staticImport(GenericMatcher.class,"recursive");
			return new Computation(recursiveMatcher(getRawName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		String keyType = getSimpleName(value.getMapKeyType());
		String valueType = getSimpleName(value.getMapValueType());
		if (value.isEmpty()) {
			imports.staticImport(MapMatcher.class, "noEntries");

			String noEntriesMatcher = noEntriesMatcher(keyType, valueType);

			return new Computation(noEntriesMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			imports.staticImport(MapMatcher.class, "containsEntries");

			Map<Computation, Computation> elements = value.entrySet().stream()
				.collect(toMap(entry -> getSimpleValue(entry.getKey()), entry -> getSimpleValue(entry.getValue())));

			List<String> entryComputations = elements.entrySet().stream()
				.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
				.collect(toList());

			Set<Entry<String, String>> entryValues = elements.entrySet().stream()
				.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()))
				.entrySet();

			String containsEntriesMatcher = containsEntriesMatcher(keyType, valueType, entryValues);
			return new Computation(containsEntriesMatcher, parameterized(Matcher.class, null, wildcard()), entryComputations);
		}
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (!computed.add(value)) {
			imports.staticImport(GenericMatcher.class,"recursive");
			return new Computation(recursiveMatcher(getRawName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		if (isPrimitive(value.getComponentType())) {
			String name = value.getComponentType().getTypeName();
			imports.staticImport(PrimitiveArrayMatcher.class, name + "ArrayContaining");

			List<Computation> elements = Stream.of(value.getArray())
				.map(element -> getSimpleValue(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String primitiveArrayContainingMatcher = primitiveArrayContainingMatcher(name, elementValues);
			return new Computation(primitiveArrayContainingMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		} else {
			imports.staticImport(Matchers.class, "arrayContaining");

			List<Computation> elements = Stream.of(value.getArray())
				.map(element -> getSimpleValue(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String arrayContainingMatcher = arrayContainingMatcher(elementValues);
			return new Computation(arrayContainingMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}

	}

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		imports.staticImport(Matchers.class, "equalTo");

		String valueExpression = asLiteral(value.getValue());

		String equalToMatcher = equalToMatcher(valueExpression);
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		imports.staticImport(Matchers.class, "nullValue");

		String nullMatcher = nullMatcher();
		return new Computation(nullMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

	@Override
	public Computation visitBigDecimal(SerializedBigDecimal value) {
		imports.registerImport(BigDecimal.class);
		imports.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toPlainString());

		String bigDecimalLiteral = newObject("BigDecimal", literal);

		String equalToMatcher = equalToMatcher(bigDecimalLiteral);
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

	@Override
	public Computation visitBigInteger(SerializedBigInteger value) {
		imports.registerImport(BigInteger.class);
		imports.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toString());

		String bigIntegerLiteral = newObject("BigInteger", literal);

		String equalToMatcher = equalToMatcher(bigIntegerLiteral);
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

	@Override
	public Computation visitUnknown(SerializedValue value) {
		return Computation.NULL;
	}

	private boolean isSimpleValue(SerializedValue element) {
		return element instanceof SerializedNull
			|| element instanceof SerializedLiteral;
	}

	private Computation getSimpleValue(SerializedValue element) {
		if (element instanceof SerializedNull) {
			return new Computation("null");
		} else if (element instanceof SerializedLiteral) {
			return new Computation(asLiteral(((SerializedLiteral) element).getValue()));
		} else {
			return element.accept(this);
		}
	}

	public static class Factory implements SerializedValueVisitorFactory {

		@Override
		public SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, ImportManager imports) {
			return new ObjectToMatcherCode(locals, imports);
		}

		@Override
		public Type resultType(Type type) {
			return parameterized(Matcher.class, null, type);
		}
	}

}
