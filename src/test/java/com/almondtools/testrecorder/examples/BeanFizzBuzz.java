package com.almondtools.testrecorder.examples;

import com.almondtools.testrecorder.Snapshot;

public class BeanFizzBuzz {

	private String last;

	public BeanFizzBuzz() {
	}
	
	public void setLast(String last) {
		this.last = last;
	}

	public static void main(String[] args){

		BeanFizzBuzz fizzBuzz = new BeanFizzBuzz();
		for(int i= 1; i <= 100; i++){
			fizzBuzz.fizzBuzz(i);
		}
	}
	
	@Snapshot
	public void fizzBuzz(int i) {
		if (i % 15 == 0) {
			last = "FizzBuzz";
		} else if (i % 3 == 0) {
			last = "Fizz";
		} else if (i % 5 == 0) {
			last = "Buzz";
		} else {
			last = String.valueOf(i);
		}
		System.out.println(last);
	}
	
	
}