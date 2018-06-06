package net.amygdalum.testrecorder.conditions;

import static net.amygdalum.testrecorder.runtime.DefaultComparisonStrategy.all;
import static net.amygdalum.testrecorder.runtime.ListEnabledComparisonStrategy.extendByLists;

import java.util.function.Predicate;

import org.assertj.core.api.Condition;

import net.amygdalum.testrecorder.runtime.GenericComparison;
import net.amygdalum.testrecorder.types.SerializedValue;

public class ASerializedValue<T extends SerializedValue> extends Condition<T> {

	public ASerializedValue(Predicate<T> predicate, String description, Object... args) {
		super(predicate, description, args);
	}

	public static <T extends SerializedValue> ASerializedValue<T> structurallyEqualTo(T expected) {
		return new ASerializedValue<>(actual -> GenericComparison.equals("", expected, actual, extendByLists(all())), " structurally equal to %s", expected);
	}

}
