package net.amygdalum.testrecorder.data;

public class FixedShortValueGenerator implements TestValueGenerator<Short> {
	
	private short value;

	public FixedShortValueGenerator(short value) {
		this.value = value;
	}

	@Override
	public Short create(TestDataGenerator generator) {
		return value;
	}

}
