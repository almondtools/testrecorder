package net.amygdalum.testrecorder.scenarios;

import java.util.List;

import net.amygdalum.testrecorder.hints.Builder;

@Builder(builder = BuildableWithDependencies.Builder.class)
public class BuildableWithDependencies {
	private List<Integer> a;
	private Buildable b;

	@Override
	public String toString() {
		return a + ":" + b;
	}

	public static class Builder {
		private BuildableWithDependencies build;

		public Builder() {
			this.build = new BuildableWithDependencies();
		}

		public BuildableWithDependencies.Builder withA(List<Integer> a) {
			build.a = a;
			return this;
		}

		public BuildableWithDependencies.Builder withB(Buildable b) {
			build.b = b;
			return this;
		}

		public BuildableWithDependencies build() {
			return build;
		}
	}

}