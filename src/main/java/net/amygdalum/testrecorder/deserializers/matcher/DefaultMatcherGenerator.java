package net.amygdalum.testrecorder.deserializers.matcher;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;

public abstract class DefaultMatcherGenerator<T extends SerializedValue> extends DefaultAdaptor<T, MatcherGenerators> implements MatcherGenerator<T> {

}
