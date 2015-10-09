package com.almondtools.testrecorder.generator;

import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.values.SerializedObject;
import com.almondtools.testrecorder.visitors.Computation;

public interface CustomGenerator {

	boolean supports(SerializedObject value);
	
	Computation deserialize(SerializedObject value, SerializedValueVisitor<Computation> parent);

}
