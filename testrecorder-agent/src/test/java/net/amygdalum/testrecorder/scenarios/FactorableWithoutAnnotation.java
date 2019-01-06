package net.amygdalum.testrecorder.scenarios;

public class FactorableWithoutAnnotation {
	private int a;
	private String b;

	@Override
	public String toString() {
		return a + ":" + b;
	}

	public static FactorableWithoutAnnotation create(int a, String b) {
		FactorableWithoutAnnotation factorable = new FactorableWithoutAnnotation();
		factorable.a = a;
		factorable.b = b;
		return factorable;
	}
}
