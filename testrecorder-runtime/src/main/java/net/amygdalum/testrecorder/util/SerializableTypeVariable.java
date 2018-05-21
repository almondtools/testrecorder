package net.amygdalum.testrecorder.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class SerializableTypeVariable<D extends GenericDeclaration> implements TypeVariable<D>, Serializable {

	private String name;
	private D genericDeclaration;
	private Type[] bounds;

	public SerializableTypeVariable(String name, D genericDeclaration, Type[] bounds) {
		this.name = name;
		this.genericDeclaration = genericDeclaration;
		this.bounds = bounds;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type[] getBounds() {
		return bounds;
	}

	@Override
	public D getGenericDeclaration() {
		return genericDeclaration;
	}

	@Override
	public Annotation[] getAnnotations() {
		return new Annotation[0];
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return new Annotation[0];
	}

	@Override
	public AnnotatedType[] getAnnotatedBounds() {
		return new AnnotatedType[0];
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

}
