package net.amygdalum.testrecorder.data;

public class RandomStringValueGenerator extends RandomValueGenerator<String> {
	
	private String[] values;

	public RandomStringValueGenerator(String... values) {
		this.values = values;
	}
	
	@Override
	public String create(TestDataGenerator generator) {
		int index = random.nextInt(values.length);
		return values[index];
	}

}
