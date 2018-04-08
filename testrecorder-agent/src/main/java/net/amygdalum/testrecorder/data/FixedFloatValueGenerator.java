package net.amygdalum.testrecorder.data;

public class FixedFloatValueGenerator implements TestValueGenerator<Float> {
	
	private float value;

	public FixedFloatValueGenerator(float value) {
		this.value = value;
	}

	@Override
	public Float create(TestDataGenerator generator) {
		return value;
	}

}
