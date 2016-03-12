package net.amygdalum.testrecorder.data;

public class FixedStringValueGenerator implements TestValueGenerator<String> {
	
	private String value;

	public FixedStringValueGenerator(String value) {
		this.value = value;
	}

	@Override
	public String create(TestDataGenerator generator) {
		return value;
	}

}
