package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.util.GenericObjectException;

public class Wrapped {

	private Class<?> clazz;
	private Object o;

	public Wrapped(String name) {
		try {
			this.clazz = Class.forName(name);
			this.o = GenericObject.newInstance(clazz);
		} catch (ReflectiveOperationException e) {
			throw new GenericObjectException(e);
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
