package net.amygdalum.testrecorder.types;

public interface ReferenceTypeVisitor<T> {

	T visitAggregateType(SerializedAggregateType value);

	T visitMapType(SerializedMapType value);

	T visitStructuralType(SerializedStructuralType value);

	T visitImmutableType(SerializedImmutableType value);

}
