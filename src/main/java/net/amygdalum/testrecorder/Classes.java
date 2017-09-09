package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.ClassDescription;
import net.amygdalum.testrecorder.profile.ClassesByName;
import net.amygdalum.testrecorder.profile.ClassesByPackage;

public interface Classes {

	boolean matches(Class<?> type);
	
	boolean matches(String className);

	static Classes byName(String name) {
		return new ClassesByName(name);
	}

	static Classes byPackage(String name) {
		return new ClassesByPackage(name);
	}

	static Classes byDescription(String className) {
		return new ClassDescription(className);
	}

}
