package com.almondtools.invivoderived.scenarios;

import com.almondtools.invivoderived.analyzer.Snapshot;

public class SideEffects {

	private int i;

	public SideEffects() {
	}
	
	public static void main(String[] args){
		SideEffects sideEffects = new SideEffects();
		for(int i= 0; i < 100; i+=sideEffects.i){
			sideEffects.method(i);
		}
		System.out.println(sideEffects.i);
	}
	
	@Snapshot
	public void method(int i) {
		this.i = i + 1;
	}
	
}