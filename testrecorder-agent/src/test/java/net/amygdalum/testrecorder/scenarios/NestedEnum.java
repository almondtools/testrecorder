package net.amygdalum.testrecorder.scenarios;

public class NestedEnum {

	private int value;
	private Nested enumValue;

	public NestedEnum(String s) {
		this.enumValue = Nested.valueOf(s);
	}

	public void inc() {
		value++;
	}

	public int getValue() {
		return value;
	}

	public String getEnumValue() {
		return enumValue.name();
	}
	
	public Nested unwrap() {
		return enumValue;
	}

	private static enum Nested {
		FIRST, SECOND;
	}

}