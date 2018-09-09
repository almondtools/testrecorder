package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.RoleVisitor;

public interface Deserializer extends RoleVisitor<Computation>{
	DeserializerContext getContext();
}
