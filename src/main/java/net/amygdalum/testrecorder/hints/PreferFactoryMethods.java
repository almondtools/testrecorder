package net.amygdalum.testrecorder.hints;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.DeserializationHint;
import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to keep the construction of the annotated entity in a factory method (default would be inline in the test)
 * 
 * This is a future feature.
 */
public class PreferFactoryMethods implements DeserializationHint {

    @Override
    public <T extends SerializedValue, G extends Deserializer<Computation>> Computation tryDeserialize(T value, G generator, Adaptors<G> adaptors) {
        throw new DeserializationException(value.toString());
    }

}
