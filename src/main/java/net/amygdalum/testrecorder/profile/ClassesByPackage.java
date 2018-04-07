package net.amygdalum.testrecorder.profile;

import org.objectweb.asm.Type;

public class ClassesByPackage implements Classes {

	private String name;

	public ClassesByPackage(String name) {
		this.name = name;
	}

	@Override
	public boolean matches(Class<?> type) {
		return type.getName().startsWith(name);
	}

	@Override
	public boolean matches(String className) {
		return Type.getObjectType(className).getClassName().startsWith(name);
	}

}
