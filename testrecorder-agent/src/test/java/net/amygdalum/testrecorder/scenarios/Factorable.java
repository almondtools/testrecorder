package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.hints.Factory;

@Factory(clazz=Factorable.class, method="create")
public class Factorable {
	private int a;
	private String b;

	@Override
	public String toString() {
		return a + ":" + b;
	}

	public static Factorable create(String b, int a) {
		Factorable factorable = new Factorable();
		factorable.a = a;
		factorable.b = b;
		return factorable;
	}
}
