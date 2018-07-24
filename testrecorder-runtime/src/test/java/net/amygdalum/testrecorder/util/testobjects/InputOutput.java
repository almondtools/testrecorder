package net.amygdalum.testrecorder.util.testobjects;

import java.util.LinkedList;
import java.util.Queue;

public class InputOutput {

	public static Queue<Character> IN = new LinkedList<>();
	public static Queue<Character> OUT = new LinkedList<>();
	
	public char in() {
		if (IN.isEmpty()) {
			return 0;
		}
		return IN.remove();
	}
	
	public void out(char c) {
		OUT.add(c);
	}
	
	public String readLowerCase() {
		StringBuilder s = new StringBuilder();
		char c = 0;
		while ((c = in()) != 0) {
			s.append(Character.toLowerCase(c));
		}
		return s.toString();
	}
	
	public void writeUpperCase(String s) {
		for (char c : s.toCharArray()) {
			out(Character.toUpperCase(c));
		}
	}
	
}
