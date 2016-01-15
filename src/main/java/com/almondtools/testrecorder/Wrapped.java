package com.almondtools.testrecorder;

import com.almondtools.testrecorder.util.GenericObject;

public class Wrapped {

	private Class<?> clazz;
	private Object o;

	public Wrapped(String name) {
		try {
			this.clazz = Class.forName(name);
			this.o = GenericObject.newInstance(clazz);
		} catch (ReflectiveOperationException | RuntimeException e) {
		}
	}

	public static Wrapped clazz(String name) {
		return new Wrapped(name);
	}

	public Class<?> getWrappedClass() {
		return clazz;
	}

	public void setField(String name, Object value) {
		GenericObject.setField(o, name, value);
	}

	public Object value() {
		return o;
	}

}
