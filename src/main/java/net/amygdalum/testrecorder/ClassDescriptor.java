package net.amygdalum.testrecorder;

public class ClassDescriptor {
	
	private String pkg;
	private String simpleName;
	private String canonicalName;

	private ClassDescriptor(Class<?> clazz) {
		this.pkg = clazz.getPackage().getName();
		this.simpleName = clazz.getSimpleName();
		this.canonicalName = clazz.getCanonicalName();
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
		return canonicalName.hashCode();
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
		return this.canonicalName.equals(that.canonicalName);
	}
	
	@Override
	public String toString() {
	    return canonicalName;
	}

}
