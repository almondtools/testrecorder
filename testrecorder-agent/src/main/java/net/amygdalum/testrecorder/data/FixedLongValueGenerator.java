package net.amygdalum.testrecorder.data;

public class FixedLongValueGenerator implements TestValueGenerator<Long> {
	
	private long value;

	public FixedLongValueGenerator(long value) {
		this.value = value;
	}

	@Override
	public Long create(TestDataGenerator generator) {
		return value;
	}

}
