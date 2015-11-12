package com.almondtools.testrecorder.visitors;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.stringtemplate.v4.ST;

public final class Templates {

	private static final String GENERIC_OBJECT_CONVERTER = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.as(<type>.class)";
	private static final String FIELD = "<type> <name> = <value>;";
	private static final String ARRAY_LITERAL = "new <type>{<elements; separator=\", \">}";
	private static final String NEW_OBJECT = "new <type>(<args; separator=\", \">)";

	private static final String ASSIGN_STMT = "<type> <name> = <value>;";
	private static final String CALL_METHOD_STMT = "<base>.<method>(<arguments; separator=\", \">);";
	private static final String RETURN_STMT = "return <value>;";

	private static final String GENERIC_TYPE = "$type$<$typeParam; separator=\", \"$>";

	private static final String GENERIC_OBJECT_MATCHER = "new GenericMatcher() {\n<fields; separator=\"\\n\">\n}.matching(<type>.class)";
	private static final String CONTAINS_MATCHER = "contains(<values; separator=\", \">)";
	private static final String EMPTY_MATCHER = "empty()";
	private static final String CONTAINS_IN_ANY_ORDER_MATCHER = "containsInAnyOrder(<values; separator=\", \">)";
	private static final String EQUAL_TO_MATCHER = "equalTo(<value>)";
	private static final String NULL_MATCHER = "nullValue()";
	private static final String NO_ENTRIES_MATCHER = "noEntries(<keytype>.class, <valuetype>.class)";
	private static final String CONTAINS_ENTRIES_MATCHER = "containsEntries(<keytype>.class, <valuetype>.class)<entries : { entry | .entry(<entry.key>, <entry.value>)}>";
	private static final String ARRAY_CONTAINING_MATCHER = "arrayContaining(<values; separator=\", \">)";
	private static final String PRIMITIVE_ARRAY_CONTAINING = "<type>ArrayContaining(<values; separator=\", \">)";


	private Templates() {
	}

	public static String assignField(String type, String name, String value) {
		ST statement = new ST(FIELD);
		statement.add("type", type);
		statement.add("name", name);
		statement.add("value", value);

		return statement.render();
	}

	public static String newObject(String type, String... arguments) {
		ST bean = new ST(NEW_OBJECT);
		bean.add("type", type);
		bean.add("args", asList(arguments));

		return bean.render();
	}

	public static String arrayLiteral(String type, List<String> elements) {
		ST statement = new ST(ARRAY_LITERAL);
		statement.add("type", type);
		statement.add("elements", elements);
		return statement.render();
	}

	public static String assignStatement(String type, String name, String value) {
		ST assign = new ST(ASSIGN_STMT);
		assign.add("type", type);
		assign.add("name", name);
		assign.add("value", value);

		return assign.render();
	}

	public static String callMethodStatement(String base, String method, String... arguments) {
		ST call = new ST(CALL_METHOD_STMT);
		call.add("base", base);
		call.add("method", method);
		call.add("arguments", asList(arguments));

		return call.render();
	}

	public static String returnStatement(String value) {
		ST assign = new ST(RETURN_STMT);
		assign.add("value", value);

		return assign.render();
	}

	public static String genericObjectConverter(String type, List<String> fields) {
		ST genericObject = new ST(GENERIC_OBJECT_CONVERTER);
		genericObject.add("type", type);
		genericObject.add("fields", fields);

		return genericObject.render();
	}

	public static String genericObjectMatcher(String type, List<String> fields) {
		ST matcher = new ST(GENERIC_OBJECT_MATCHER);
		matcher.add("type", type);
		matcher.add("fields", fields);

		return matcher.render();
	}

	public static String genericType(String type, String... typeParams) {
		ST genericType = new ST(GENERIC_TYPE, '$','$');
		genericType.add("type", type);
		genericType.add("typeParam", asList(typeParams));

		return genericType.render();
	}

	public static String containsMatcher(String... values) {
		ST matcher = new ST(CONTAINS_MATCHER);
		matcher.add("values", asList(values));

		return matcher.render();
	}

	public static String containsInAnyOrderMatcher(String... values) {
		ST matcher = new ST(CONTAINS_IN_ANY_ORDER_MATCHER);
		matcher.add("values", values);

		return matcher.render();
	}

	public static String noEntriesMatcher(String keyType, String valueType) {
		ST matcher = new ST(NO_ENTRIES_MATCHER);
		matcher.add("keytype", keyType);
		matcher.add("valuetype", valueType);

		return matcher.render();
	}

	public static String containsEntriesMatcher(String keyType, String valueType, Set<Entry<String, String>> entryValues) {
		ST matcher = new ST(CONTAINS_ENTRIES_MATCHER);
		matcher.add("keytype", keyType);
		matcher.add("valuetype", valueType);
		matcher.add("entries", entryValues);

		return matcher.render();
	}

	public static String emptyMatcher() {
		ST matcher = new ST(EMPTY_MATCHER);

		return matcher.render();
	}

	public static String arrayContainingMatcher(String... elementValues) {
		ST matcher = new ST(ARRAY_CONTAINING_MATCHER);
		matcher.add("values", asList(elementValues));

		return matcher.render();
	}

	public static String primitiveArrayContainingMatcher(String type, String... elementValues) {
		ST matcher = new ST(PRIMITIVE_ARRAY_CONTAINING);
		matcher.add("type", type);
		matcher.add("values", asList(elementValues));

		return matcher.render();
	}

	public static String equalToMatcher(String value) {
		ST matcher = new ST(EQUAL_TO_MATCHER);
		matcher.add("value", value);

		return matcher.render();
	}

	public static String nullMatcher() {
		ST matcher = new ST(NULL_MATCHER);

		return matcher.render();
	}

}
