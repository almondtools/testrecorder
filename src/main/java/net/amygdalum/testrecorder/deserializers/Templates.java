package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;

import java.util.List;

import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.util.Pair;

public final class Templates {

	private static final String GENERIC_OBJECT = "new GenericObject() {\n<fields; separator=\"\\n\">\n}";
	private static final String GENERIC_OBJECT_CONVERTER = "new GenericObject() {\n<fields; separator=\"\\n\">\n}.as(<type>)";
	private static final String ARRAY_LITERAL = "new <type>{<elements; separator=\", \">}";
	private static final String NEW_OBJECT = "new <type>(<args; separator=\", \">)";
	private static final String NEW_ARRAY = "new <type>[<len>]";
	private static final String FIELD_ACCESS_EXP = "<base>.<field>";
	private static final String CALL_METHOD_EXP = "<base>.<method>(<arguments; separator=\", \">)";
	private static final String CALL_LOCAL_METHOD_EXP = "<method>(<arguments; separator=\", \">)";
	private static final String CALL_METHOD_CHAIN_EXP = "<base>.<methods;separator=\".\">";
	private static final String CAST_EXP = "(<type>) <expression>";

	private static final String NEW_ANONYMOUS_CLASS_INSTANCE = "new <type>(<args; separator=\", \">) {\n<body>\n}";
	private static final String FIELD_DECLARATION = "<if(modifiers)><modifiers> <endif><type> <name><if(value)> = <value><endif>;";
	private static final String METHOD_DECLARATION = "<if(modifiers)><modifiers> <endif><returntype> <name>(<args; separator=\", \">) {\n<body>\n}";
	private static final String EXPRESSION_STMT = "<value>;";
	private static final String ASSIGN_FIELD_STMT = "<base>.<field> = <value>;";
	private static final String ASSIGN_LOCAL_VARIABLE_STMT = "<if(type)><type> <endif><name> = <value>;";
	private static final String CALL_METHOD_STMT = "<base>.<method>(<arguments; separator=\", \">);";
	private static final String CALL_LOCAL_METHOD_STMT = "<method>(<arguments; separator=\", \">);";
	private static final String RETURN_STMT = "return <value>;";

	private static final String CAPTURE_EXCEPTION = "capture(() -> {<statements>}, <type>)";

	private static final String PARAM = "<type> <name>";
    private static final String ANNOTATION = "@<annotation><if(values)>(<values : {value | <value.element1> = <value.element2>}; separator=\", \">)<endif>";

	private static final String GENERIC_OBJECT_MATCHER = "new GenericMatcher() {\n<fields; separator=\"\\n\">\n}.matching(<type : {type | <type>}; separator=\", \">)";
	private static final String WIDENING_MATCHER = "widening(<value>)";
	private static final String ENUM_MATCHER = "matchingEnum(<value>)";
	private static final String RECURSIVE_MATCHER = "recursive(<type>)";
	private static final String LAMBDA_MATCHER = "lambda(<name>)";
	private static final String CONTAINS_IN_ORDER_MATCHER = "containsInOrder(<type>, <values; separator=\", \">)";
	private static final String EMPTY_MATCHER = "empty()";
	private static final String CONTAINS_IN_ANY_ORDER_MATCHER = "contains(<type>, <values; separator=\", \">)";
	private static final String EQUAL_TO_MATCHER = "equalTo(<value>)";
	private static final String SAME_INSTANCE_MATCHER = "sameInstance(<value>)";
	private static final String NULL_MATCHER = "nullValue(<value>)";
	private static final String NO_ENTRIES_MATCHER = "noEntries(<keytype>, <valuetype>)";
	private static final String CONTAINS_ENTRIES_MATCHER = "containsEntries(<keytype>, <valuetype>)<entries : { entry | .entry(<entry.element1>, <entry.element2>)}>";
	private static final String ARRAY_CONTAINING_MATCHER = "arrayContaining(<type>, <values; separator=\", \">)";
	private static final String ARRAY_EMPTY_MATCHER = "emptyArray()";
	private static final String PRIMITIVE_ARRAY_CONTAINING_MATCHER = "<type>ArrayContaining(<values; separator=\", \">)";
	private static final String PRIMITIVE_ARRAY_EMPTY_MATCHER = "<type>EmptyArray()";


	private Templates() {
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
		return newObject(type, asList(arguments));
	}

	public static String newObject(String type, List<String> arguments) {
		ST bean = new ST(NEW_OBJECT);
		bean.add("type", type);
		bean.add("args", arguments);

		return bean.render();
	}

	public static String newArray(String type, String len) {
		ST statement = new ST(NEW_ARRAY);
		statement.add("type", type);
		statement.add("len", len);
		

		return statement.render();
	}

	public static String arrayLiteral(String type, List<String> elements) {
		ST statement = new ST(ARRAY_LITERAL);
		statement.add("type", type);
		statement.add("elements", elements);
		
		return statement.render();
	}

	public static String newAnonymousClassInstance(String type, List<String> arguments, String body) {
		ST object = new ST(NEW_ANONYMOUS_CLASS_INSTANCE);
		object.add("type", type);
		object.add("args", arguments);
		object.add("body", body);

		return object.render();
	}

	public static String fieldDeclaration(String modifiers, String type, String name, String value) {
		ST field = new ST(FIELD_DECLARATION);
		field.add("modifiers", modifiers);
		field.add("type", type);
		field.add("name", name);
		field.add("value", value);

		return field.render();
	}

	public static String fieldDeclaration(String modifiers, String type, String name) {
		return fieldDeclaration(modifiers, type, name, null);
	}

	public static String methodDeclaration(String modifiers, String type, String name, List<String> args, String body) {
		ST method = new ST(METHOD_DECLARATION);
		method.add("modifiers", modifiers);
		method.add("returntype", type);
		method.add("name", name);
		method.add("args", args);
		method.add("body", body);

		return method.render();
	}

	public static String param(String type, String name) {
		ST field = new ST(PARAM);
		field.add("type", type);
		field.add("name", name);

		return field.render();
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

	public static String callMethodChainExpression(String base, List<String> methods) {
		ST call = new ST(CALL_METHOD_CHAIN_EXP);
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

	public static String widening(String value) {
		ST matcher = new ST(WIDENING_MATCHER);
		matcher.add("value", value);

		return matcher.render();
	}

	public static String containsInOrderMatcher(String elementType, String... values) {
		ST matcher = new ST(CONTAINS_IN_ORDER_MATCHER);
		matcher.add("type", elementType);
		matcher.add("values", asList(values));

		return matcher.render();
	}

	public static String containsInAnyOrderMatcher(String elementType, String... values) {
		ST matcher = new ST(CONTAINS_IN_ANY_ORDER_MATCHER);
		matcher.add("type", elementType);
		matcher.add("values", values);

		return matcher.render();
	}

	public static String noEntriesMatcher(String keyType, String valueType) {
		ST matcher = new ST(NO_ENTRIES_MATCHER);
		matcher.add("keytype", keyType);
		matcher.add("valuetype", valueType);

		return matcher.render();
	}

	public static String containsEntriesMatcher(String keyType, String valueType, List<Pair<String, String>> entryValues) {
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

	public static String lambdaMatcher(String name) {
		ST matcher = new ST(LAMBDA_MATCHER);
		matcher.add("name", name);
		
		return matcher.render();
	}
	
	public static String arrayContainingMatcher(String type, String... elementValues) {
		ST matcher = new ST(ARRAY_CONTAINING_MATCHER);
		matcher.add("type", type);
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

    @SafeVarargs
	public static String annotation(String annotation, Pair<String, String>... values) {
    	return annotation(annotation, asList(values));
	}

    public static String annotation(String annotation, List<Pair<String, String>> values) {
        ST matcher = new ST(ANNOTATION);
        matcher.add("annotation", annotation);
        matcher.add("values", values);

        return matcher.render();
    }

}
