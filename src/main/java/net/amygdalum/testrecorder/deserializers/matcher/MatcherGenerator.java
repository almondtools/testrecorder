package net.amygdalum.testrecorder.deserializers.matcher;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.types.SerializedValue;

public interface MatcherGenerator<T extends SerializedValue> extends Adaptor<T, MatcherGenerators> {

}
