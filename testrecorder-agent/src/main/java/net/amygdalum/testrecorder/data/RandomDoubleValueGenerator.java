package net.amygdalum.testrecorder.data;

public class RandomDoubleValueGenerator extends RandomValueGenerator<Double> {
	
	@Override
	public Double create(TestDataGenerator generator) {
		return Double.longBitsToDouble(random.nextLong());
	}

}
