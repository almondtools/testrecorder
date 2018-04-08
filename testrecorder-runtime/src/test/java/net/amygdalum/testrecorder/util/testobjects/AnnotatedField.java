package net.amygdalum.testrecorder.util.testobjects;

public class AnnotatedField {

	@MyAnnotation
	private String annotated;

	private Annotated annotatedValue;

	public String getAnnotated() {
		return annotated;
	}

	public Annotated getAnnotatedValue() {
		return annotatedValue;
	}
}