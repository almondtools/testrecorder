package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.stringtemplate.v4.ST;

public final class Templates {

	private static final String GENERIC_OBJECT = "new GenericObject() {\n<fields; separator=\"\\n\">\n}";
	private static final String GENERIC_OBJECT_CONVERTER = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.as(<type>)";
	private static final String ARRAY_LITERAL = "new <type>{<elements; separator=\", \">}";
	private static final String NEW_OBJECT = "new <type>(<args; separator=\", \">)";
	private static final String FIELD_ACCESS_EXP = "<base>.<field>";
	private static final String CALL_METHOD_EXP = "<base>.<method>(<arguments; separator=\", \">)";
	private static final String CALL_LOCAL_METHOD_EXP = "<method>(<arguments; separator=\", \">)";
	private static final String CAST_EXP = "(<type>) <expression>";

	private static final String FIELD_DECLARATION = "<modifiers> <type> <name>;";
	private static final String EXPRESSION_STMT = "<value>;";
	private static final String ASSIGN_FIELD_STMT = "<base>.<field> = <value>;";
	private static final String ASSIGN_LOCAL_VARIABLE_STMT = "<type> <name> = <value>;";
	private static final String CALL_METHOD_STMT = "<base>.<method>(<arguments; separator=\", \">);";
	private static final String CALL_METHOD_CHAIN_STMT = "<base>.<methods;separator=\".\">;";
	private static final String CALL_LOCAL_METHOD_STMT = "<method>(<arguments; separator=\", \">);";
	private static final String RETURN_STMT = "return <value>;";

	private static final String CAPTURE_EXCEPTION = "capture(() -> {<statements>}, <type>)";

	private static final String GENERIC_TYPE = "$type$<$typeParam; separator=\", \"$>";

	private static final String GENERIC_OBJECT_MATCHER = "new GenericMatcher() {\n<fields; separator=\"\\n\">\n}.matching(<type : {type | <type>}; separator=\", \">)";
	private static final String ENUM_MATCHER = "matchingEnum(<value>)";
	private static final String RECURSIVE_MATCHER = "recursive(<type>)";
	private static final String CONTAINS_MATCHER = "contains(<values; separator=\", \">)";
	private static final String EMPTY_MATCHER = "empty()";
	private static final String CONTAINS_IN_ANY_ORDER_MATCHER = "containsInAnyOrder(<values; separator=\", \">)";
	private static final String EQUAL_TO_MATCHER = "equalTo(<value>)";
	private static final String SAME_INSTANCE_MATCHER = "sameInstance(<value>)";
	private static final String NULL_MATCHER = "nullValue(<value>)";
	private static final String NO_ENTRIES_MATCHER = "noEntries(<keytype>.class, <valuetype>.class)";
	private static final String CONTAINS_ENTRIES_MATCHER = "containsEntries(<keytype>.class, <valuetype>.class)<entries : { entry | .entry(<entry.key>, <entry.value>)}>";
	private static final String ARRAY_CONTAINING_MATCHER = "arrayContaining(<values; separator=\", \">)";
	private static final String ARRAY_EMPTY_MATCHER = "emptyArray()";
	private static final String PRIMITIVE_ARRAY_CONTAINING_MATCHER = "<type>ArrayContaining(<values; separator=\", \">)";
	private static final String PRIMITIVE_ARRAY_EMPTY_MATCHER = "<type>EmptyArray()";


	private Templates() {
	}

	public static String asLiteral(Character c) {
		StringBuilder buffer = new StringBuilder();
		buffer.append('\'');
		if (c == '\n') {
			buffer.append("\\n");
		} else if (c == '\r') {
			buffer.append("\\r");
		} else if (c == '\\') {
			buffer.append("\\\\");
		} else if (c == '\'') {
			buffer.append("\\'");
		} else if (c < 0x20 || c > 0x7f) {
			buffer.append("\\u");
			if (c < 0x10) {
				buffer.append("000");
			} else if (c < 0x100) {
				buffer.append("00");
			} else if (c < 0x1000) {
				buffer.append('0');
			}
			buffer.append(Integer.toString(c, 16));
		} else {
			buffer.append(c);
		}
		buffer.append('\'');
		return buffer.toString();
	}

	public static String asLiteral(String rawString) {
		StringBuilder buffer = new StringBuilder();
		buffer.append('\"');
		for (int i = 0; i < rawString.length(); ++i) {
			char c = rawString.charAt(i);
			if (c == '\n') {
				buffer.append("\\n");
			} else if (c == '\r') {
				buffer.append("\\r");
			} else if (c == '\\') {
				buffer.append("\\\\");
			} else if (c == '"') {
				buffer.append("\\\"");
			} else if (c < 0x20 || c > 0x7f) {
				buffer.append("\\u");
				if (c < 0x10) {
					buffer.append("000");
				} else if (c < 0x100) {
					buffer.append("00");
				} else if (c < 0x1000) {
					buffer.append('0');
				}
				buffer.append(Integer.toString(c, 16));
			} else {
				buffer.append(c);
			}
		}
		buffer.append('\"');
		return buffer.toString();
	}

	public static String asLiteral(Float f) {
		if (Float.isFinite(f)) {
			return f.toString() + "f";
		} else if (f.isNaN()) {
			return "Float.NaN";
		} else if (f.floatValue() == Float.POSITIVE_INFINITY) {
			return "Float.POSITIVE_INFINITY";
		} else if (f.floatValue() == Float.NEGATIVE_INFINITY) {
			return "Float.NEGATIVE_INFINITY";
		} else {
			return f.toString() + "f";
		}
	}

	public static String asLiteral(Double d) {
		if (Double.isFinite(d)) {
			return d.toString();
		} else if (d.isNaN()) {
			return "Double.NaN";
		} else if (d.doubleValue() == Double.POSITIVE_INFINITY) {
			return "Double.POSITIVE_INFINITY";
		} else if (d.doubleValue() == Double.NEGATIVE_INFINITY) {
			return "Double.NEGATIVE_INFINITY";
		} else {
			return d.toString();
		}
	}

	public static String asLiteral(Object value) {
		if (value instanceof String) {
			return asLiteral((String) value);
		} else if (value instanceof Character) {
			return asLiteral((Character) value);
		} else if (value instanceof Byte) {
			return "(byte) " + value.toString();
		} else if (value instanceof Short) {
			return "(short) " + value.toString();
		} else if (value instanceof Float) {
			return asLiteral((Float) value);
		} else if (value instanceof Long) {
			return value.toString() + "l";
		} else if (value instanceof Double) {
			return asLiteral((Double) value);
		} else {
			return value.toString();
		}
	}

	public static String classOf(String name) {
		return name + ".class";
	}

	public static String stringOf(String name) {
		return asLiteral(name);
	}

	public static String expressionStatement(String value) {
		ST statement = new ST(EXPRESSION_STMT);
		statement.add("value", value);

		return statement.render();
	}

	public static String fieldAccess(String base, String field) {
		ST statement = new ST(FIELD_ACCESS_EXP);
		statement.add("base", base);
		statement.add("field", field);

		return statement.render();
	}

	public static String callMethod(String base, String method, String... arguments) {
		return callMethod(base, method, asList(arguments));
	}

	public static String callMethod(String base, String method, List<String> arguments) {
		ST statement = new ST(CALL_METHOD_EXP);
		statement.add("base", base);
		statement.add("method", method);
		statement.add("arguments", arguments);

		return statement.render();
	}

	public static String callLocalMethod(String method, String... arguments) {
		return callLocalMethod(method, asList(arguments));
	}

	public static String callLocalMethod(String method, List<String> arguments) {
		ST statement = new ST(CALL_LOCAL_METHOD_EXP);
		statement.add("method", method);
		statement.add("arguments", arguments);

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

	public static String fieldDeclaration(String modifiers, String type, String name) {
		ST assign = new ST(FIELD_DECLARATION);
		assign.add("modifiers", modifiers);
		assign.add("type", type);
		assign.add("name", name);

		return assign.render();
	}

	public static String assignLocalVariableStatement(String type, String name, String value) {
		ST assign = new ST(ASSIGN_LOCAL_VARIABLE_STMT);
		assign.add("type", type);
		assign.add("name", name);
		assign.add("value", value);
		
		return assign.render();
	}
	
	public static String assignFieldStatement(String base, String field, String value) {
		ST assign = new ST(ASSIGN_FIELD_STMT);
		assign.add("base", base);
		assign.add("field", field);
		assign.add("value", value);

		return assign.render();
	}

	public static String callMethodStatement(String base, String method, List<String> arguments) {
		ST call = new ST(CALL_METHOD_STMT);
		call.add("base", base);
		call.add("method", method);
		call.add("arguments", arguments);

		return call.render();
	}

	public static String callMethodChainStatement(String base, List<String> methods) {
		ST call = new ST(CALL_METHOD_CHAIN_STMT);
		call.add("base", base);
		call.add("methods", methods);

		return call.render();
	}

	public static String callLocalMethodStatement(String method, String... arguments) {
		return callLocalMethodStatement(method, asList(arguments));
	}

	public static String callLocalMethodStatement(String method, List<String> arguments) {
		ST call = new ST(CALL_LOCAL_METHOD_STMT);
		call.add("method", method);
		call.add("arguments", arguments);

		return call.render();
	}

	public static String callMethodStatement(String base, String method, String... arguments) {
		return callMethodStatement(base, method, asList(arguments));
	}

	public static String returnStatement(String value) {
		ST assign = new ST(RETURN_STMT);
		assign.add("value", value);

		return assign.render();
	}

	public static String captureException(List<String> statements, String type) {
		ST assign = new ST(CAPTURE_EXCEPTION);
		assign.add("statements", statements);
		assign.add("type", type);

		return assign.render();
	}

	public static String genericObject(String type, List<String> fields) {
		ST genericObject = new ST(GENERIC_OBJECT);
		genericObject.add("type", type);
		genericObject.add("fields", fields);

		return genericObject.render();
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

	public static String genericObjectMatcher(String type, String to, List<String> fields) {
		ST matcher = new ST(GENERIC_OBJECT_MATCHER);
		matcher.add("type", asList(type, to));
		matcher.add("fields", fields);

		return matcher.render();
	}

	public static String enumMatcher(String value) {
		ST matcher = new ST(ENUM_MATCHER);
		matcher.add("value", value);

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

	public static String recursiveMatcher(String type) {
		ST matcher = new ST(RECURSIVE_MATCHER);
		matcher.add("type", type);

		return matcher.render();
	}

	public static String arrayContainingMatcher(String... elementValues) {
		ST matcher = new ST(ARRAY_CONTAINING_MATCHER);
		matcher.add("values", asList(elementValues));

		return matcher.render();
	}

	public static String arrayEmptyMatcher() {
		ST matcher = new ST(ARRAY_EMPTY_MATCHER);

		return matcher.render();
	}

	public static String primitiveArrayContainingMatcher(String type, String... elementValues) {
		ST matcher = new ST(PRIMITIVE_ARRAY_CONTAINING_MATCHER);
		matcher.add("type", type);
		matcher.add("values", asList(elementValues));

		return matcher.render();
	}

	public static String primitiveArrayEmptyMatcher(String type) {
		ST matcher = new ST(PRIMITIVE_ARRAY_EMPTY_MATCHER);
		matcher.add("type", type);

		return matcher.render();
	}

	public static String equalToMatcher(String value) {
		ST matcher = new ST(EQUAL_TO_MATCHER);
		matcher.add("value", value);

		return matcher.render();
	}

	public static String sameInstanceMatcher(String value) {
		ST matcher = new ST(SAME_INSTANCE_MATCHER);
		matcher.add("value", value);

		return matcher.render();
	}

	public static String nullMatcher(String value) {
		ST matcher = new ST(NULL_MATCHER);
		matcher.add("value", value);

		return matcher.render();
	}

	public static String cast(String type, String expression) {
		ST matcher = new ST(CAST_EXP);
		matcher.add("type", type);
		matcher.add("expression", expression);

		return matcher.render();
	}

}
