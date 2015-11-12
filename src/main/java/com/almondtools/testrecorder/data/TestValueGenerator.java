package com.almondtools.testrecorder.data;

public interface TestValueGenerator<T> {

	T create(TestDataGenerator generator);

}
