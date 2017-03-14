package net.amygdalum.testrecorder.hints;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.DeserializationHint;
import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to skip the generation of a matcher for this entity (e.g. if this entity is known not to be relevant for the result)
 * 
 * This is a future feature.
 */
public class SkipChecks implements DeserializationHint {

    @Override
    public <T extends SerializedValue, G extends Deserializer<Computation>> Computation tryDeserialize(T value, G generator, Adaptors<G> adaptors) {
        throw new DeserializationException(value.toString());
    }

}
