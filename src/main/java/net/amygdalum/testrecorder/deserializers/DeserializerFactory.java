package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.Deserializer;

public interface DeserializerFactory {

	Deserializer<Computation> create(LocalVariableNameGenerator locals, TypeManager types);

	Type resultType(Type type);
}
