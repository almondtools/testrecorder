package net.amygdalum.testrecorder.profile;

import net.amygdalum.testrecorder.Packages;

public class PackagesByName implements Packages {

	private String name;

	public PackagesByName(String name) {
		this.name = name;
	}

	@Override
	public boolean matches(Package pkg) {
		return pkg.getName().equals(name);
	}

	@Override
	public boolean matches(String packageName) {
		return packageName.equals(name);
	}

}
