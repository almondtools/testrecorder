package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.extensionpoint.ExtensionStrategy.EXTENDING;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;
import net.amygdalum.testrecorder.types.SerializedValue;

@ExtensionPoint(strategy=EXTENDING)
public interface SetupGenerator<T extends SerializedValue> extends Adaptor<T> {

}
