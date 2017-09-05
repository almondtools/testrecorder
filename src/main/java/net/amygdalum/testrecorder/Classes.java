package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.ClassesByName;

public interface Classes {

	boolean matches(Class<?> type);
	
	boolean matches(String className);

	static Classes byName(String name) {
		return new ClassesByName(name);
	}

}
