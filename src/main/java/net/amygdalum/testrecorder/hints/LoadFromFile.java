package net.amygdalum.testrecorder.hints;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.DeserializationHint;
import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to load the construction of the entity from file (using some kind of serialization reader)
 * 
 * This is a future feature.
 */
public class LoadFromFile implements DeserializationHint {

    @Override
    public <T extends SerializedValue, G extends Deserializer<Computation>> Computation tryDeserialize(T value, G generator, Adaptors<G> adaptors) {
        throw new DeserializationException(value.toString());
    }

}
