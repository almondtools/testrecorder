package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;
import java.util.stream.Stream;

public class SerializableTypeVariable<D extends GenericDeclaration> implements TypeVariable<D>, Serializable {

	private String name;
	private D genericDeclaration;
	private Type[] bounds;

	public SerializableTypeVariable(String name, D genericDeclaration) {
		this.name = name;
		this.genericDeclaration = genericDeclaration;
		this.bounds = new Type[0];
	}

	public SerializableTypeVariable<D> boundedBy(Type... bounds) {
		this.bounds = bounds;
		return this;
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
		return name.hashCode() * 37
			+ (genericDeclaration == null ? 0 : genericDeclaration.hashCode() * 17)
			+ 31;
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
		if (!this.name.equals(that.name)) {
			return false;
		}
		if (!Objects.equals(this.genericDeclaration, that.getGenericDeclaration())) {
			return false;
		}
		if (this.bounds.length != that.bounds.length) {
			return false;
		}
		for (int i = 0; i < bounds.length; i++) {
			if (!Objects.equals(baseType(this.bounds[i]), baseType(that.bounds[i]))) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(name);
		String boundsStr = Stream.of(bounds)
			.filter(type -> type != Object.class)
			.map(type -> baseType(type).getTypeName())
			.collect(joining(", "));
		if (!boundsStr.isEmpty()) {
			buffer.append(" extends ").append(boundsStr);
		}
		return buffer.toString();
	}

}
