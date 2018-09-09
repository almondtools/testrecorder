package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.types.DeserializerContext;

public interface DeserializerFactory {

	Deserializer newGenerator(DeserializerContext context);

}
