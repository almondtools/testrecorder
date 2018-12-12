package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.extensionpoint.ExtensionStrategy.EXTENDING;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;
import net.amygdalum.testrecorder.types.SerializedValue;

@ExtensionPoint(strategy=EXTENDING)
public interface MatcherGenerator<T extends SerializedValue> extends Adaptor<T> {

}
