package com.almondtools.testrecorder.visitors;

import com.almondtools.testrecorder.SerializedValueVisitor;

public interface SerializedValueVisitorFactory {

	SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, ImportManager imports);

}
