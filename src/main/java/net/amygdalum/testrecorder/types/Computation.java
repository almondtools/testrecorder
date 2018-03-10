package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Computation {

	public static final Computation NULL = new Computation("", null, false, new ArrayList<>());
	
	private List<String> statements;
	private String value;
	private Type type;
	private boolean stored;

	public Computation(String value, Type type, boolean stored, List<String> statements) {
		this.value = value;
		this.type = type;
		this.stored = stored;
		this.statements = statements;
	}
	
	public static Computation expression(String value, Type type) {
		return new Computation(value, type, false, new ArrayList<>());
	}
	
	public static Computation expression(String value, Type type, List<String> statements) {
		return new Computation(value, type, false, statements);
	}
	
	public static Computation variable(String value, Type type) {
		return new Computation(value, type, true, new ArrayList<>());
	}

	public static Computation variable(String value, Type type, List<String> statements) {
		return new Computation(value, type, true, statements);
	}

	public String getValue() {
		return value;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isStored() {
		return stored;
	}
	
	public List<String> getStatements() {
		return statements;
	}

}
