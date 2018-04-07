package net.amygdalum.testrecorder.profile;

import java.util.regex.Pattern;

import org.objectweb.asm.Type;

public class ClassesByName implements Classes {

	private static final Pattern NAME = Pattern.compile("[\\w$.]+");

	private String name;

	public ClassesByName(String name) {
		if (!NAME.matcher(name).matches()) {
			throw new IllegalArgumentException("class name should contain only word characters, dot and $, but was: " + name);
		}
		this.name = name;
	}

	@Override
	public boolean matches(Class<?> type) {
		return type.getName().equals(name)
			|| type.getSimpleName().equals(name);
	}

	@Override
	public boolean matches(String className) {
		String refName = Type.getObjectType(className).getClassName();
		int lastDot = refName.lastIndexOf('.');
		String simpleName = refName.substring(lastDot + 1);
		return refName.equals(name)
			|| simpleName.equals(name);
	}

}
