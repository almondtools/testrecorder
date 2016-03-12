package net.amygdalum.testrecorder.data;

public class RandomByteValueGenerator extends RandomValueGenerator<Byte> {
	
	@Override
	public Byte create(TestDataGenerator generator) {
		return (byte) random.nextInt();
	}

}
