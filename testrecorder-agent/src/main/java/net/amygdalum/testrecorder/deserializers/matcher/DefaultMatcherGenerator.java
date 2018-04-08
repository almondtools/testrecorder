package net.amygdalum.testrecorder.deserializers.matcher;

import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.types.SerializedValue;

public abstract class DefaultMatcherGenerator<T extends SerializedValue> extends DefaultAdaptor<T, MatcherGenerators> implements MatcherGenerator<T> {

}
