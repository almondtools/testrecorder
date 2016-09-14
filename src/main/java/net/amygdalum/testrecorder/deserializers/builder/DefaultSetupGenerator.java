package net.amygdalum.testrecorder.deserializers.builder;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;

public abstract class DefaultSetupGenerator<T extends SerializedValue> extends DefaultAdaptor<T, SetupGenerators> implements SetupGenerator<T> {

}
