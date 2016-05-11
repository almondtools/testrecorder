package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.util.ClassInstrumenting;
import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.util.GenericObjectException;

public class Wrapped {

	private Class<?> clazz;
	private Object o;

	public Wrapped(String name) {
		this.clazz = classForName(name);
		this.o = clazz.isInterface() || clazz.isEnum() ? null : GenericObject.newInstance(clazz);
	}

	public static Class<?> classForName(String name) {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null || !(loader instanceof ClassInstrumenting)) {
				loader = Wrapped.class.getClassLoader();
			}
			return loader.loadClass(name);
		} catch (ClassNotFoundException e) {
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
