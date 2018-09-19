package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.AnnotatedElement;

public class CustomAnnotation {

	private AnnotatedElement target;
	private Object annotation;

	public CustomAnnotation(AnnotatedElement target, Object annotation) {
		this.target = target;
		this.annotation = annotation;
	}

	public AnnotatedElement getTarget() {
		return target;
	}

	public Object getAnnotation() {
		return annotation;
	}

	@Override
	public int hashCode() {
		return 17
			+ target.hashCode() * 13
			+ annotation.hashCode() * 29;
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
		CustomAnnotation that = (CustomAnnotation) obj;
		return this.target.equals(that.target)
			&& this.annotation.equals(that.annotation);
	}

}
