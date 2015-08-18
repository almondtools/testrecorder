package com.almondtools.invivoderived.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.almondtools.invivoderived.analyzer.Snapshot;

public class FizzBuzz {

	private List<String> buffer;

	public FizzBuzz() {
		this(new ArrayList<>());
	}
	
	public FizzBuzz(List<String> buffer) {
		this.buffer = buffer;
	}

	public static void main(String[] args){

		FizzBuzz fizzBuzz = new FizzBuzz();
		for(int i= 1; i <= 100; i++){
			fizzBuzz.fizzBuzz(i);
		}
		System.out.println(fizzBuzz.buffer.stream()
			.collect(Collectors.joining("\n", "", "\n")));
	}
	
	@Snapshot
	public void fizzBuzz(int i) {
		if (i % 15 == 0) {
			buffer.add("FizzBuzz");
		} else if (i % 3 == 0) {
			buffer.add("Fizz");
		} else if (i % 5 == 0) {
			buffer.add("Buzz");
		} else {
			buffer.add(String.valueOf(i));
		}
	}
	
	public List<String> getBuffer() {
		return buffer;
	}
	
	
}