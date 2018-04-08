package net.amygdalum.testrecorder.data;

public class RandomCharValueGenerator extends RandomValueGenerator<Character> {
	
	public RandomCharValueGenerator() {
	}

	@Override
	public Character create(TestDataGenerator generator) {
		return (char) random.nextInt();
	}

}
