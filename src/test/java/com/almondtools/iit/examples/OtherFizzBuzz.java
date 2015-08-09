package com.almondtools.iit.examples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.almondtools.iit.Snapshot;

public class OtherFizzBuzz {

	private Set<String> notInvolvedSet;
	private Map<String, String> notInvolvedMap;
	private Set<String> nullSet;
	private String[] notInvolvedArray;
	
	public OtherFizzBuzz() {
		this.notInvolvedSet = new HashSet<>();
		this.notInvolvedMap = new HashMap<>();
		notInvolvedMap.put("key", "value");
		this.notInvolvedArray = new String[0];
		this.nullSet = null;
	}
	
	public Set<String> getNotInvolvedSet() {
		return notInvolvedSet;
	}
	
	public Map<String, String> getNotInvolvedMap() {
		return notInvolvedMap;
	}
	
	public String[] getNotInvolvedArray() {
		return notInvolvedArray;
	}
	
	public Set<String> getNullSet() {
		return nullSet;
	}

	public static void main(String[] args){
		OtherFizzBuzz fizzBuzz = new OtherFizzBuzz();
		for(int i= 1; i <= 100; i++){
			System.out.println(fizzBuzz.fizzBuzz(i));
		}
	}
	
	@Snapshot
	public String fizzBuzz(int i) {
		if (i % 15 == 0) {
			return "FizzBuzz";
		} else if (i % 3 == 0) {
			return "Fizz";
		} else if (i % 5 == 0) {
			return "Buzz";
		} else {
			return String.valueOf(i);
		}
	}

}