package net.amygdalum.testrecorder.util.testobjects;

public abstract class NestedAbstract {

	private int id;

	public NestedAbstract() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static NestedPublic createNestedPublic() {
		return new NestedPublic();
	}

	public static NestedPublicNonPublicConstructor createNestedPublicNonPublicConstructor() {
		return new NestedPublicNonPublicConstructor();
	}

	public static NestedPackagePrivatePublicConstructor createNestedPackagePrivatePublicConstructor() {
		return new NestedPackagePrivatePublicConstructor();
	}
	
	public static class NestedPublic extends NestedAbstract {

		public NestedPublic() {
		}
		
	}

	public static class NestedPublicNonPublicConstructor extends NestedAbstract {

		NestedPublicNonPublicConstructor() {
		}
		
	}

	static class NestedPackagePrivatePublicConstructor extends NestedAbstract {

		public NestedPackagePrivatePublicConstructor() {
		}
}
}