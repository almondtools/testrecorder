package net.amygdalum.testrecorder.data;

public class RandomLongValueGenerator extends RandomValueGenerator<Long> {
	
	@Override
	public Long create(TestDataGenerator generator) {
		return random.nextLong();
	}

}
