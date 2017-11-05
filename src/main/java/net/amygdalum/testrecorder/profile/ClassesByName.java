package net.amygdalum.testrecorder.profile;

import org.objectweb.asm.Type;

import net.amygdalum.testrecorder.Classes;

public class ClassesByName implements Classes {

	private String name;

	public ClassesByName(String name) {
		this.name = name;
	}

	@Override
	public boolean matches(Class<?> type) {
		return type.getName().equals(name);
	}

	@Override
	public boolean matches(String className) {
		return Type.getObjectType(className).getClassName().equals(name);
	}

}
