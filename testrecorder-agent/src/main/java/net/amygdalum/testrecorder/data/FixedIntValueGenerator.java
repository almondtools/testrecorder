package net.amygdalum.testrecorder.data;

public class FixedIntValueGenerator implements TestValueGenerator<Integer> {
	
	private int value;

	public FixedIntValueGenerator(int value) {
		this.value = value;
	}

	@Override
	public Integer create(TestDataGenerator generator) {
		return value;
	}

}
