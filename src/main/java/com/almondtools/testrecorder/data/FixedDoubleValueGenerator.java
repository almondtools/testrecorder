package com.almondtools.testrecorder.data;

public class FixedDoubleValueGenerator implements TestValueGenerator<Double> {
	
	private double value;

	public FixedDoubleValueGenerator(double value) {
		this.value = value;
	}

	@Override
	public Double create(TestDataGenerator generator) {
		return value;
	}

}
