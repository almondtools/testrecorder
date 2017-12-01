package net.amygdalum.testrecorder.deserializers.builder;

import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.types.SerializedValue;

public abstract class DefaultSetupGenerator<T extends SerializedValue> extends DefaultAdaptor<T, SetupGenerators> implements SetupGenerator<T> {

}
