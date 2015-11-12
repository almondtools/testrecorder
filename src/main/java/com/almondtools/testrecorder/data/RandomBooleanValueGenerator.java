package com.almondtools.testrecorder.data;

public class RandomBooleanValueGenerator extends RandomValueGenerator<Boolean> {
	
	public RandomBooleanValueGenerator() {
	}

	@Override
	public Boolean create(TestDataGenerator generator) {
		return random.nextBoolean();
	}

}
