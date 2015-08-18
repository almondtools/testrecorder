package com.almondtools.invitroderivatives.scenarios;

import com.almondtools.invitroderivatives.analyzer.Snapshot;

public class Results {

	public Results() {
	}

	public static void main(String[] args) {
		Results pow = new Results();
		for (int i = 1; i <= 100; i++) {
			System.out.println(pow.pow(i));
		}
	}

	@Snapshot
	public double pow(int i) {
		return Math.pow(i, i);
	}
}