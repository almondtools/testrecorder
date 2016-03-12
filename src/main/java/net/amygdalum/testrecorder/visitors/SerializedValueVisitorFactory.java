package net.amygdalum.testrecorder.visitors;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedValueVisitor;

public interface SerializedValueVisitorFactory {

	SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, TypeManager types);

	Type resultType(Type type);
}
