package com.almondtools.testrecorder.data;

public class RandomShortValueGenerator extends RandomValueGenerator<Short> {
	
	@Override
	public Short create(TestDataGenerator generator) {
		return (short) random.nextInt();
	}

}
