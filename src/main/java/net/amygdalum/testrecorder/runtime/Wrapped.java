package net.amygdalum.testrecorder.runtime;

import net.amygdalum.testrecorder.util.ClassInstrumenting;

public class Wrapped {

	private Class<?> clazz;
	private Object o;

	private Wrapped(Class<?> clazz, Object o) {
		this.clazz = clazz;
		this.o = o;
	}

	public static Class<?> classForName(String name) {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null || !(loader instanceof ClassInstrumenting)) {
				loader = Wrapped.class.getClassLoader();
			}
            return loader.loadClass(name);
		} catch (ClassNotFoundException e) {
			throw new GenericObjectException("wrapped class <" + name + "> not found", e);
		}
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

	public static Wrapped clazz(String name) {
		Class<?> clazz = classForName(name);
		Object o = clazz.isInterface() || clazz.isEnum() ? null : GenericObject.newInstance(clazz);
		return new Wrapped(clazz, o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Wrapped enumType(String name, String value) {
		Class<?> clazz = classForName(name);
		Object o = clazz.isEnum() ? Enum.valueOf((Class<? extends Enum>) clazz, value) : null;
		return new Wrapped(clazz, o);
	}

}