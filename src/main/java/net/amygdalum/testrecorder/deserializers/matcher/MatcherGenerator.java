package net.amygdalum.testrecorder.deserializers.matcher;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Adaptor;

public interface MatcherGenerator<T extends SerializedValue> extends Adaptor<T, MatcherGenerators> {

}
