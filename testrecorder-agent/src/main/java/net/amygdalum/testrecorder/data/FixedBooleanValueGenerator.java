package net.amygdalum.testrecorder.data;

public class FixedBooleanValueGenerator implements TestValueGenerator<Boolean> {
	
	private boolean value;

	public FixedBooleanValueGenerator(boolean value) {
		this.value = value;
	}

	@Override
	public Boolean create(TestDataGenerator generator) {
		return value;
	}

}
