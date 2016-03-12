package net.amygdalum.testrecorder.data;

public class FixedByteValueGenerator implements TestValueGenerator<Byte> {
	
	private byte value;

	public FixedByteValueGenerator(byte value) {
		this.value = value;
	}

	@Override
	public Byte create(TestDataGenerator generator) {
		return value;
	}

}
