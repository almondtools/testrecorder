package com.almondtools.iit.visitors;

import static com.almondtools.iit.runtime.TemplateHelper.asLiteral;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.stringtemplate.v4.ST;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.SerializedValueVisitor;
import com.almondtools.iit.runtime.MapMatcher;
import com.almondtools.iit.values.SerializedArray;
import com.almondtools.iit.values.SerializedField;
import com.almondtools.iit.values.SerializedList;
import com.almondtools.iit.values.SerializedLiteral;
import com.almondtools.iit.values.SerializedMap;
import com.almondtools.iit.values.SerializedNull;
import com.almondtools.iit.values.SerializedObject;
import com.almondtools.iit.values.SerializedSet;

public class ObjectToMatcherCode implements SerializedValueVisitor<Computation> {

	private static final String ASSERT = "assertThat(<object>,<matcher>);";

	private static final String GENERIC_OBJECT = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.matcher(<type>.class)";
	private static final String FIELD = "<type> <name> = <value>;";
	private static final String MATCHER = "Matcher<$type$>";

	private static final String NULL = "nullValue()";
	private static final String EQUAL_TO = "equalTo(<value>)";
	private static final String EMPTY = "empty()";
	private static final String CONTAINS = "contains(<values; separator=\", \">)";
	private static final String NO_ENTRIES = "noEntries()";
	private static final String CONTAINS_ENTRIES = "containsEntries().<entries : { entry | entry(<entry.key>, <entry.value>)}>";
	private static final String ARRAY_CONTAINING = "arrayContaining(<values; separator=\", \">)";

	private LocalVariableNameGenerator locals;

	private Set<String> imports;

	public ObjectToMatcherCode() {
		this(new LocalVariableNameGenerator());
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals) {
		this.locals = locals;
		this.imports = new LinkedHashSet<>();
	}

	public LocalVariableNameGenerator getLocals() {
		return locals;
	}

	public Set<String> getImports() {
		return imports;
	}

	public List<String> createAssertion(SerializedValue o, String exp) {
		imports.add("static " + Assert.class.getName() + ".assertThat");
		imports.add(Matcher.class.getName());

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
			fieldEntry.add("type", field.getType().getSimpleName());
			fieldEntry.add("name", field.getName());
			fieldEntry.add("value", value.getValue());

			return new Computation(fieldEntry.render(), value.getStatements());
		} else {
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
		matcher.add("type", value.getType().getName());
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
			imports.add("static " + Matchers.class.getName() + ".empty");

			ST matcher = new ST(EMPTY);

			return new Computation(matcher.render(), emptyList());
		} else {
			imports.add("static " + Matchers.class.getName() + ".contains");

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
			imports.add("static " + Matchers.class.getName() + ".empty");

			ST matcher = new ST(EMPTY);

			return new Computation(matcher.render(), emptyList());
		} else {
			imports.add("static " + Matchers.class.getName() + ".contains");

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
	public Computation visitMap(SerializedMap value) {
		if (value.isEmpty()) {
			imports.add("static " + MapMatcher.class.getName() + ".noEntries");

			ST matcher = new ST(NO_ENTRIES);

			return new Computation(matcher.render(), emptyList());
		} else {
			imports.add("static " + MapMatcher.class.getName() + ".containsEntries");

			Map<Computation, Computation> elements = value.entrySet().stream()
				.collect(toMap(entry -> getSimpleValue(entry.getKey()), entry -> getSimpleValue(entry.getValue())));

			ST matcher = new ST(CONTAINS_ENTRIES);
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
		imports.add("static " + Matchers.class.getName() + ".arrayContaining");

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

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		imports.add("static " + Matchers.class.getName() + ".equalTo");

		ST matcher = new ST(EQUAL_TO);
		matcher.add("value", literalValue(value.getValue()));

		return new Computation(matcher.render(), emptyList());
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		imports.add("static " + CoreMatchers.class.getName() + ".nullValue");

		ST matcher = new ST(NULL);

		return new Computation(matcher.render(), emptyList());
	}

	private boolean isSimpleValue(SerializedValue element) {
		return element instanceof SerializedNull
			|| element instanceof SerializedLiteral;
	}

	private Computation getSimpleValue(SerializedValue element) {
		if (element instanceof SerializedNull) {
			return new Computation("null");
		} else if (element instanceof SerializedLiteral) {
			return new Computation(literalValue(((SerializedLiteral) element).getValue()));
		} else {
			return element.accept(this);
		}
	}

	private String literalValue(Object value) {
		if (value instanceof String) {
			return asLiteral((String) value);
		} else {
			return value.toString();
		}
	}

	private String matcherType(Class<?> clazz) {
		if (clazz == List.class || clazz == Set.class || clazz == Map.class) {
			return "?";
		} else {
			return clazz.getSimpleName();
		}
	}

}
