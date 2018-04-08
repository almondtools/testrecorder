package net.amygdalum.testrecorder.profile;

import org.objectweb.asm.Type;

public class ClassDescription implements Classes {

	private String className;

	public ClassDescription(String className) {
		this.className = className;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		String className = Type.getInternalName(clazz);
		return this.className.equals(className);
	}

	@Override
	public boolean matches(String className) {
		return this.className.equals(className);
	}

}
