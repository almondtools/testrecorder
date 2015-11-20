package com.almondtools.testrecorder.visitors;

import java.lang.reflect.Type;

import com.almondtools.testrecorder.SerializedValueVisitor;

public interface SerializedValueVisitorFactory {

	SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, ImportManager imports);

	Type resultType(Type type);
}
