package net.amygdalum.testrecorder.deserializers;

import java.util.function.Function;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedKeyValue;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedValueType;

public class MappedRoleVisitor<T, S> implements RoleVisitor<T> {

	private RoleVisitor<S> visitor;
	private Function<S, T> mapping;

	public MappedRoleVisitor(RoleVisitor<S> visitor, Function<S, T> mapping) {
		this.visitor = visitor;
		this.mapping = mapping;
	}

	@Override
	public T visitField(SerializedField field) {
		return mapping.apply(field.accept(visitor));
	}

	@Override
	public T visitKeyValue(SerializedKeyValue keyvalue) {
		return mapping.apply(keyvalue.accept(visitor));
	}

	@Override
	public T visitArgument(SerializedArgument argument) {
		return mapping.apply(argument.accept(visitor));
	}

	@Override
	public T visitResult(SerializedResult result) {
		return mapping.apply(result.accept(visitor));
	}

	@Override
	public T visitReferenceType(SerializedReferenceType value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitImmutableType(SerializedImmutableType value) {
		return mapping.apply(value.accept(visitor));
	}

	@Override
	public T visitValueType(SerializedValueType value) {
		return mapping.apply(value.accept(visitor));
	}

}
