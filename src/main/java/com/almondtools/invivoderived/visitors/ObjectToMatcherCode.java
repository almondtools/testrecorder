package com.almondtools.invivoderived.visitors;

import static com.almondtools.invivoderived.generator.TemplateHelper.asLiteral;
import static com.almondtools.invivoderived.generator.TypeHelper.getSimpleName;
import static com.almondtools.invivoderived.generator.TypeHelper.isPrimitive;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.stringtemplate.v4.ST;

import com.almondtools.invivoderived.SerializedCollectionVisitor;
import com.almondtools.invivoderived.SerializedImmutableVisitor;
import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.SerializedValueVisitor;
import com.almondtools.invivoderived.generator.MapMatcher;
import com.almondtools.invivoderived.generator.PrimitiveArrayMatcher;
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

public class ObjectToMatcherCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

	private static final String ASSERT = "assertThat(<object>,<matcher>);";

	private static final String GENERIC_OBJECT = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.matcher(<type>.class)";
	private static final String NEW_OBJECT = "new <type>(<args; separator=\", \">)";
	private static final String FIELD = "<type> <name> = <value>;";
	private static final String MATCHER = "Matcher<$type$>";

	private static final String NULL = "nullValue()";
	private static final String EQUAL_TO = "equalTo(<value>)";
	private static final String EMPTY = "empty()";
	private static final String CONTAINS = "contains(<values; separator=\", \">)";
	private static final String CONTAINS_IN_ANY_ORDER = "containsInAnyOrder(<values; separator=\", \">)";
	private static final String NO_ENTRIES = "noEntries(<keytype>.class, <valuetype>.class)";
	private static final String CONTAINS_ENTRIES = "containsEntries(<keytype>.class, <valuetype>.class)<entries : { entry | .entry(<entry.key>, <entry.value>)}>";
	private static final String ARRAY_CONTAINING = "arrayContaining(<values; separator=\", \">)";
	private static final String PRIMITIVE_ARRAY_CONTAINING = "<type>ArrayContaining(<values; separator=\", \">)";

	private LocalVariableNameGenerator locals;
	private ImportManager imports;

	public ObjectToMatcherCode() {
		this(new LocalVariableNameGenerator(), new ImportManager());
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals, ImportManager imports) {
		this.locals = locals;
		this.imports = imports;
	}

	public LocalVariableNameGenerator getLocals() {
		return locals;
	}

	public ImportManager getImports() {
		return imports;
	}

	public List<String> createAssertion(SerializedValue o, String exp) {
		imports.staticImport(Assert.class, "assertThat");

		List<String> statements = new ArrayList<>();

		Computation matcher = o.accept(this);

		statements.addAll(matcher.getStatements());

		ST assertion = new ST(ASSERT);
		assertion.add("object", exp);
		assertion.add("matcher", matcher.getValue());

		statements.add(assertion.render());

		return statements;
	}

	@Override
	public Computation visitField(SerializedField field) {
		SerializedValue fieldValue = field.getValue();
		if (isSimpleValue(fieldValue)) {
			Computation value = getSimpleValue(fieldValue);

			ST fieldEntry = new ST(FIELD);
			fieldEntry.add("type", getSimpleName(field.getType()));
			fieldEntry.add("name", field.getName());
			fieldEntry.add("value", value.getValue());

			return new Computation(fieldEntry.render(), value.getStatements());
		} else {
			imports.registerImport(Matcher.class);
			Computation value = fieldValue.accept(this);

			ST matcher = new ST(MATCHER, '$', '$');
			matcher.add("type", matcherType(field.getType()));

			ST fieldEntry = new ST(FIELD);
			fieldEntry.add("type", matcher);
			fieldEntry.add("name", field.getName());
			fieldEntry.add("value", value.getValue());

			return new Computation(fieldEntry.render(), value.getStatements());
		}
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		List<Computation> fields = value.getFields().stream()
			.map(field -> field.accept(this))
			.collect(toList());

		ST matcher = new ST(GENERIC_OBJECT);
		matcher.add("type", getSimpleName(value.getType()));
		matcher.add("fields", fields.stream()
			.map(field -> field.getValue())
			.collect(toList()));

		return new Computation(matcher.render(), fields.stream()
			.flatMap(field -> field.getStatements().stream())
			.collect(toList()));
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (value.isEmpty()) {
			imports.staticImport(Matchers.class, "empty");

			ST matcher = new ST(EMPTY);

			return new Computation(matcher.render(), emptyList());
		} else {
			imports.staticImport(Matchers.class, "contains");

			List<Computation> elements = value.stream()
				.map(element -> getSimpleValue(element))
				.collect(toList());

			ST matcher = new ST(CONTAINS);
			matcher.add("values", elements.stream()
				.map(element -> element.getValue())
				.collect(toList()));

			return new Computation(matcher.render(), elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList()));
		}
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (value.isEmpty()) {
			imports.staticImport(Matchers.class, "empty");

			ST matcher = new ST(EMPTY);

			return new Computation(matcher.render(), emptyList());
		} else {
			imports.staticImport(Matchers.class, "containsInAnyOrder");

			List<Computation> elements = value.stream()
				.map(element -> getSimpleValue(element))
				.collect(toList());

			ST matcher = new ST(CONTAINS_IN_ANY_ORDER);
			matcher.add("values", elements.stream()
				.map(element -> element.getValue())
				.collect(toList()));

			return new Computation(matcher.render(), elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList()));
		}
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (value.isEmpty()) {
			imports.staticImport(MapMatcher.class, "noEntries");

			ST matcher = new ST(NO_ENTRIES);
			matcher.add("keytype", getSimpleName(value.getKeyType()));
			matcher.add("valuetype", getSimpleName(value.getValueType()));

			return new Computation(matcher.render(), emptyList());
		} else {
			imports.staticImport(MapMatcher.class, "containsEntries");

			Map<Computation, Computation> elements = value.entrySet().stream()
				.collect(toMap(entry -> getSimpleValue(entry.getKey()), entry -> getSimpleValue(entry.getValue())));

			ST matcher = new ST(CONTAINS_ENTRIES);
			matcher.add("keytype", getSimpleName(value.getKeyType()));
			matcher.add("valuetype", getSimpleName(value.getValueType()));
			matcher.add("entries", elements.entrySet().stream()
				.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()))
				.entrySet());

			return new Computation(matcher.render(), elements.entrySet().stream()
				.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
				.collect(toList()));
		}
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (isPrimitive(value.getComponentType())) {
			String name = value.getComponentType().getTypeName();
			imports.staticImport(PrimitiveArrayMatcher.class, name + "ArrayContaining");

			List<Computation> elements = Stream.of(value.getArray())
				.map(element -> getSimpleValue(element))
				.collect(toList());

			ST matcher = new ST(PRIMITIVE_ARRAY_CONTAINING);
			matcher.add("type", name);
			matcher.add("values", elements.stream()
				.map(element -> element.getValue())
				.collect(toList()));

			return new Computation(matcher.render(), elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList()));
		} else {
			imports.staticImport(Matchers.class, "arrayContaining");

			List<Computation> elements = Stream.of(value.getArray())
				.map(element -> getSimpleValue(element))
				.collect(toList());

			ST matcher = new ST(ARRAY_CONTAINING);
			matcher.add("values", elements.stream()
				.map(element -> element.getValue())
				.collect(toList()));

			return new Computation(matcher.render(), elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList()));
		}

	}

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		imports.staticImport(Matchers.class, "equalTo");

		ST matcher = new ST(EQUAL_TO);
		matcher.add("value", asLiteral(value.getValue()));

		return new Computation(matcher.render(), emptyList());
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		imports.staticImport(Matchers.class, "nullValue");

		ST matcher = new ST(NULL);

		return new Computation(matcher.render(), emptyList());
	}
	
	@Override
	public Computation visitBigDecimal(SerializedBigDecimal value) {
		imports.registerImport(BigDecimal.class);
		imports.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toPlainString());
		ST expression = new ST(NEW_OBJECT);
		expression.add("type", "BigDecimal");
		expression.add("args", literal);
		
		ST matcher = new ST(EQUAL_TO);
		matcher.add("value", expression.render());

		return new Computation(matcher.render(), emptyList());
	}
	
	@Override
	public Computation visitBigInteger(SerializedBigInteger value) {
		imports.registerImport(BigInteger.class);
		imports.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toString());
		ST expression = new ST(NEW_OBJECT);
		expression.add("type", "BigInteger");
		expression.add("args", literal);
		
		ST matcher = new ST(EQUAL_TO);
		matcher.add("value", expression.render());

		return new Computation(matcher.render(), emptyList());
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

	private String matcherType(Type clazz) {
		if (clazz == List.class || clazz == Set.class || clazz == Map.class) {
			return "?";
		} else {
			return getSimpleName(clazz);
		}
	}

}
