package net.amygdalum.testrecorder.serializers;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.SerializedInput;
import net.amygdalum.testrecorder.types.SerializedOutput;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;

public interface SerializerFacade {

	SerializedValue serialize(Type type, Object object, SerializerSession session);

	SerializedValue[] serialize(Type[] clazzes, Object[] objects, SerializerSession session);

	SerializedOutput serializeOutput(int id, MethodSignature signature);

	SerializedInput serializeInput(int id, MethodSignature signature);

	SerializerSession newSession();

}
