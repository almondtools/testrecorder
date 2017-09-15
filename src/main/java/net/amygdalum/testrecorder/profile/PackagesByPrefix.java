package net.amygdalum.testrecorder.profile;

import net.amygdalum.testrecorder.Packages;

public class PackagesByPrefix implements Packages {

	private String name;

	public PackagesByPrefix(String name) {
		this.name = name;
	}

	@Override
	public boolean matches(Package pkg) {
		return pkg.getName().startsWith(name);
	}

	@Override
	public boolean matches(String packageName) {
		return packageName.startsWith(name);
	}

}
