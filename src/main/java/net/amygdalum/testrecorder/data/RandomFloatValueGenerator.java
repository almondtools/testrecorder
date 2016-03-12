package net.amygdalum.testrecorder.data;

public class RandomFloatValueGenerator extends RandomValueGenerator<Float> {
	
	@Override
	public Float create(TestDataGenerator generator) {
		return Float.intBitsToFloat(random.nextInt());
	}

}
