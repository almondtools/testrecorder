package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.hints.Builder;

@Builder(builder=Buildable.Builder.class)
public class Buildable {
	private int a;
	private String b;

	@Override
	public String toString() {
		return a + ":" + b;
	}

	public static class Builder {
		private Buildable build;

		public Builder() {
			this.build = new Buildable();
		}

		public Builder withA(int a) {
			build.a = a;
			return this;
		}

		public Builder withB(String b) {
			build.b = b;
			return this;
		}

		public Buildable build() {
			return build;
		}
	}
}
