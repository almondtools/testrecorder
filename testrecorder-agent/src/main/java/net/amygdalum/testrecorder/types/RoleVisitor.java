package net.amygdalum.testrecorder.types;

public interface RoleVisitor<T> {

	T visitArgument(SerializedArgument argument);

	T visitResult(SerializedResult result);

    T visitField(SerializedField field);

	T visitReferenceType(SerializedReferenceType value);

	T visitImmutableType(SerializedImmutableType value);

	T visitValueType(SerializedValueType value);

}
