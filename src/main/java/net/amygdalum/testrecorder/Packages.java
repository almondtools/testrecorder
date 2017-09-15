package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.PackagesByName;
import net.amygdalum.testrecorder.profile.PackagesByPrefix;

public interface Packages {

	boolean matches(Package pkg);

	boolean matches(String packageName);

	static Packages byName(String name) {
		return new PackagesByName(name);
	}

	static Packages byPrefix(String name) {
		return new PackagesByPrefix(name);
	}

}
