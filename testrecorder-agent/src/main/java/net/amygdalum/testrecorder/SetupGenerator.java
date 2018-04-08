package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.annotation;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.stringtemplate.v4.ST;

import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;
import net.amygdalum.testrecorder.types.TypeManager;

public class SetupGenerator {

	private static final String SETUP_TEMPLATE = "<annotations;separator=\"\\n\">\n"
		+ "public void <name>() throws Exception {\n"
		+ "  <statements;separator=\"\\n\">\n"
		+ "}\n";


	private TypeManager types;
	private String name;
	private List<String> annotations;
	private List<String> statements;

	public SetupGenerator(TypeManager types, String name, List<Class<?>> annotations) {
		this.types = types;
		types.registerTypes(annotations.toArray(new Type[0]));
		this.name = name;
		this.annotations = annotations.stream().map(type -> annotation(types.getRawTypeName(type))).collect(toList());
		this.statements = new ArrayList<>();
	}

	public SetupGenerator generateInitialize(TestRecorderAgentInitializer initializer) {
		types.registerType(initializer.getClass());
		String initObject = newObject(types.getConstructorTypeName(initializer.getClass()));
		String initStmt = callMethodStatement(initObject, "run");
		statements.add(initStmt);
		return this;
	}

	public SetupGenerator generateReset() {
		types.registerTypes(FakeIO.class);
		statements.add(callMethodStatement(types.getRawTypeName(FakeIO.class), "reset"));
		return this;
	}

	public String generateSetup() {
		ST test = new ST(SETUP_TEMPLATE);
		test.add("annotations", annotations);
		test.add("name", name);
		test.add("statements", statements);
		return test.render();
	}

}
