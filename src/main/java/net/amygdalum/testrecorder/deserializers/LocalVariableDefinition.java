package net.amygdalum.testrecorder.deserializers;

public class LocalVariableDefinition {

	private String name;
	private Progress progress;
	
	public LocalVariableDefinition(String name) {
		this.name = name;
		this.progress = Progress.ALLOCATED;
	}
	
	public String getName() {
		return name;
	}
	
	public LocalVariableDefinition define() {
		this.progress = Progress.DEFINED;
		return this;
	}
	
	public boolean isDefined() {
		return progress == Progress.DEFINED || progress == Progress.READY;
	}
	
	public LocalVariableDefinition finish() {
		this.progress = Progress.READY;
		return this;
	}
	
	public boolean isReady() {
		return progress == Progress.READY;
	}
	
	private enum Progress {
		ALLOCATED, DEFINED, READY;
	}
}
