package net.amygdalum.testrecorder.evaluator;

import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;

public interface Expression {

    Optional<SerializedValue> evaluate(SerializedValue base);
}
