package net.amygdalum.testrecorder.evaluator;

import static net.amygdalum.testrecorder.util.Types.*;

import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedList;

public class IndexExpression implements Expression {

    private String index;

    public IndexExpression(String index) {
        this.index = index;
    }

    @Override
    public Optional<SerializedValue> evaluate(SerializedValue base) {
    	return evaluate(base, null);
    }

    @Override
    public Optional<SerializedValue> evaluate(SerializedValue base, Class<?> type) {
        try {
            if (base instanceof SerializedArray) {
            	SerializedArray arrayValue = (SerializedArray) base;
				if (type != null && !type.isAssignableFrom(boxedType(arrayValue.getComponentType()))) {
            		return Optional.empty();
            	}
                int i = Integer.parseInt(index);
                SerializedValue[] array = arrayValue.getArray();
                if (i >= 0 && i < array.length) {
                    return Optional.of(array[i]);
                }
            } else if (base instanceof SerializedList) {
                SerializedList list = (SerializedList) base;
				if (type != null && !type.isAssignableFrom(boxedType(list.getComponentType()))) {
            		return Optional.empty();
            	}
                int i = Integer.parseInt(index);
                if (i >= 0 && i < list.size()) {
                    return Optional.of(list.get(i));
                }
            }
            return Optional.empty();
        } catch (NumberFormatException | NullPointerException e) {
            return Optional.empty();
        }
    }

}
