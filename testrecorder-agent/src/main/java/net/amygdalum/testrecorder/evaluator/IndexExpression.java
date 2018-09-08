package net.amygdalum.testrecorder.evaluator;

import static net.amygdalum.testrecorder.util.Types.boxedType;

import java.lang.reflect.Type;
import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedList;

public class IndexExpression implements Expression {

	private int index;

	public IndexExpression(int index) {
		this.index = index;
	}

	@Override
	public Optional<SerializedValue> evaluate(SerializedValue base) {
		return evaluate(base, null);
	}

	@Override
	public Optional<SerializedValue> evaluate(SerializedValue base, Class<?> type) {
		if (base instanceof SerializedArray) {
			return applyIndexToArray(type, (SerializedArray) base);
		} else if (base instanceof SerializedList) {
			return applyIndexToList(type, (SerializedList) base);
		} else {
			return Optional.empty();
		}
	}

	private Optional<SerializedValue> applyIndexToArray(Class<?> type, SerializedArray array) {
		if (!isApplicable(type, array.getComponentType(), array.size())) {
			return Optional.empty();
		}
		return Optional.of(array.get(index));
	}

	private Optional<SerializedValue> applyIndexToList(Class<?> type, SerializedList list) {
		if (!isApplicable(type, list.getComponentType(), list.size())) {
			return Optional.empty();
		}
		return Optional.of(list.get(index));
	}

	private boolean isApplicable(Class<?> type, Type componentType, int size) {
		if (type != null && !type.isAssignableFrom(boxedType(componentType))) {
			return false;
		}
		if (index < 0 || index >= size) {
			return false;
		}
		return true;

	}

}
