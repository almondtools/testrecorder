package net.amygdalum.testrecorder.scenarios;

public class BuildableWithoutAnnotation {
	private int a;
	private String b;

	@Override
	public String toString() {
		return a + ":" + b;
	}

	public static class Builder {
		private BuildableWithoutAnnotation build;

		public Builder() {
			this.build = new BuildableWithoutAnnotation();
		}

		public Builder withA(int a) {
			build.a = a;
			return this;
		}

		public Builder withB(String b) {
			build.b = b;
			return this;
		}

		public BuildableWithoutAnnotation build() {
			return build;
		}
	}
}
