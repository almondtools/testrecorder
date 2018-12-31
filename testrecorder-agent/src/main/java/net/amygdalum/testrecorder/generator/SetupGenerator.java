package net.amygdalum.testrecorder.generator;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;

import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.fakeio.FakeIO;
import net.amygdalum.testrecorder.types.TypeManager;

public class SetupGenerator {

	private TypeManager types;
	private String name;
	private TestTemplate template;
	private List<String> statements;

	public SetupGenerator(TypeManager types, String name, TestTemplate template, List<CustomAnnotation> annotations) {
		this.types = types;
		this.template = template;
		this.name = name;
		this.statements = new ArrayList<>();
	}

	public SetupGenerator generateReset() {
		types.registerTypes(FakeIO.class);
		statements.add(callMethodStatement(types.getRawTypeName(FakeIO.class), "reset"));
		return this;
	}

	public String generateSetup() {
		return template.setupMethod(name, types, annotations(), statements);
	}

	private List<String> annotations() {
		return emptyList();
	}
}
