package com.almondtools.iit.examples;

import com.almondtools.iit.analyzer.Snapshot;

public class Pow {

	public Pow() {
	}

	public static void main(String[] args) {
		Pow pow = new Pow();
		for (int i = 1; i <= 100; i++) {
			System.out.println(pow.pow(i));
		}
	}

	@Snapshot
	public double pow(int i) {
		return Math.pow(i, i);
	}
}