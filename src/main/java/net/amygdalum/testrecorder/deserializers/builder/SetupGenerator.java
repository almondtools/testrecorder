package net.amygdalum.testrecorder.deserializers.builder;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Adaptor;

public interface SetupGenerator<T extends SerializedValue> extends Adaptor<T, SetupGenerators> {

}
