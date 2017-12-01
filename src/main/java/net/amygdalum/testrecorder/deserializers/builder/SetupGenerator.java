package net.amygdalum.testrecorder.deserializers.builder;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.types.SerializedValue;

public interface SetupGenerator<T extends SerializedValue> extends Adaptor<T, SetupGenerators> {

}
