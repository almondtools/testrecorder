package net.amygdalum.testrecorder.runtime;

import net.amygdalum.testrecorder.util.RedefiningClassLoader;

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
			if (loader == null || !(loader instanceof RedefiningClassLoader)) {
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

	public <T> T value(Class<T> clazz) {
		return clazz.cast(o);
	}

	public static Wrapped clazz(String name) {
		Class<?> clazz = classForName(name);
		if (clazz.isInterface() || clazz.isEnum()) {
			throw new GenericObjectException("cannot wrap interfaces or enums");
		}
		return new Wrapped(clazz, GenericObject.newInstance(clazz));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Wrapped enumType(String name, String value) {
		Class<?> clazz = classForName(name);
		if (!clazz.isEnum()) {
			throw new GenericObjectException("cannot wrap non-enums");
		}
		return new Wrapped(clazz, Enum.valueOf((Class<? extends Enum>) clazz, value));
	}

}
