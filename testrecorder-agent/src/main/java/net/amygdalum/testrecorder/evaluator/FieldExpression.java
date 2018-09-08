package net.amygdalum.testrecorder.evaluator;

import static net.amygdalum.testrecorder.util.Types.boxedType;

import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class FieldExpression implements Expression {

    private String field;

    public FieldExpression(String field) {
        this.field = field;
    }

    @Override
    public Optional<SerializedValue> evaluate(SerializedValue base) {
        return evaluate(base, null);
    }

    @Override
    public Optional<SerializedValue> evaluate(SerializedValue base, Class<?> type) {
        if (base instanceof SerializedObject) {
            SerializedObject object = (SerializedObject) base;
            Stream<SerializedField> selector = object.getFields().stream()
                .filter(f -> f.getName().equals(field));
            if (type != null) {
            	selector = selector.filter(f -> type.isAssignableFrom(boxedType(f.getType())));
            }
			return selector
                .map(f -> f.getValue())
                .findFirst();
        }
        return Optional.empty();
    }
    
}
