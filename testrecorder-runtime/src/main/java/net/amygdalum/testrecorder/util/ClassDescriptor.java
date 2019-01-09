package net.amygdalum.testrecorder.util;

import java.util.Objects;

public class ClassDescriptor {
	
	private String pkg;
	private String simpleName;

	private ClassDescriptor(Class<?> clazz) {
		this.pkg = clazz.getPackage().getName();
		this.simpleName = clazz.getSimpleName();
	}
	
	public static ClassDescriptor of(Class<?> clazz) {
		return new ClassDescriptor(clazz);
	}

	public String getPackage() {
		return pkg;
	}

	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public int hashCode() {
		return (pkg == null ? 0 : pkg.hashCode())
			+ simpleName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClassDescriptor that = (ClassDescriptor) obj;
		
		return Objects.equals(this.pkg, that.pkg)
			&& this.simpleName.equals(that.simpleName);
	}
	
	@Override
	public String toString() {
	    return pkg + "." + simpleName;
	}

}
