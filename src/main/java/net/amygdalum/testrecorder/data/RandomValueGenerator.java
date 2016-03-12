package net.amygdalum.testrecorder.data;

import java.util.Random;

public abstract class RandomValueGenerator<T> implements TestValueGenerator<T>{

	protected Random random;
	
	public RandomValueGenerator() {
		this.random = new Random();
	}

}
