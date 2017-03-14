package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;

public interface DeserializationHint {

    <T extends SerializedValue, G extends Deserializer<Computation>> Computation tryDeserialize(T value, G generator, Adaptors<G> adaptors);

}
