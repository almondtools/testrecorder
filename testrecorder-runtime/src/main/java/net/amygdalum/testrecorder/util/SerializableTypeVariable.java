package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

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

	@Override
	public int hashCode() {
		return name.hashCode() * 37 + (genericDeclaration == null ? 0 : genericDeclaration.hashCode() * 17) + Arrays.hashCode(bounds) * 3 + 31;
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
		SerializableTypeVariable<?> that = (SerializableTypeVariable<?>) obj;
		return this.name.equals(that.name)
			&& Objects.equals(this.genericDeclaration, that.getGenericDeclaration())
			&& Arrays.equals(this.bounds, that.bounds);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("name");
		if (bounds.length > 0) {
			buffer.append(" extends ").append(Stream.of(bounds)
				.filter(type -> type != Object.class)
				.map(type -> type.getTypeName())
				.collect(joining(", ")));
		}
		return buffer.toString();
	}

}
