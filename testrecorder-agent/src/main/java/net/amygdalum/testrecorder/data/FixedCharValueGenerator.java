package net.amygdalum.testrecorder.data;

public class FixedCharValueGenerator implements TestValueGenerator<Character> {
	
	private char value;

	public FixedCharValueGenerator(char value) {
		this.value = value;
	}

	@Override
	public Character create(TestDataGenerator generator) {
		return value;
	}

}
