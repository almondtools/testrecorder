package net.amygdalum.testrecorder.evaluator;

import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedObject;

public class FieldExpression implements Expression {

    private String field;

    public FieldExpression(String field) {
        this.field = field;
    }

    @Override
    public Optional<SerializedValue> evaluate(SerializedValue base) {
        if (base instanceof SerializedObject) {
            SerializedObject object = (SerializedObject) base;
            return object.getFields().stream()
                .filter(f -> f.getName().equals(field))
                .map(f -> f.getValue())
                .findFirst();
        }
        return Optional.empty();
    }

}
