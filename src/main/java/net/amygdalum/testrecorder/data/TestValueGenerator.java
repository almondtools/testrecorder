package net.amygdalum.testrecorder.data;

public interface TestValueGenerator<T> {

	T create(TestDataGenerator generator);

}
