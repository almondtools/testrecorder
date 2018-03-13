package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;

public class LocalVariable {

	private String name;
	private Type type;
	private Progress progress;

	public LocalVariable(String name) {
		this.name = name;
		this.progress = Progress.ALLOCATED;
	}

	public LocalVariable(String name, Type type) {
		this.name = name;
		this.type = type;
		this.progress = Progress.ALLOCATED;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public LocalVariable define(Type type) {
		this.type = type;
		this.progress = Progress.DEFINED;
		return this;
	}

	public boolean isDefined() {
		return progress == Progress.DEFINED || progress == Progress.READY;
	}

	public LocalVariable finish() {
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
