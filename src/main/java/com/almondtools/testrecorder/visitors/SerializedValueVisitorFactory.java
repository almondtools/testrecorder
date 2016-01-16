package com.almondtools.testrecorder.visitors;

import java.lang.reflect.Type;

import com.almondtools.testrecorder.SerializedValueVisitor;

public interface SerializedValueVisitorFactory {

	SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, TypeManager types);

	Type resultType(Type type);
}
