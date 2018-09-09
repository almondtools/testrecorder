package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public class SimpleValueAdaptor extends DefaultMatcherGenerator<SerializedValue> implements MatcherGenerator<SerializedValue> {

	public boolean isSimpleValue(SerializedValue element) {
		return element instanceof SerializedNull
			|| element instanceof SerializedLiteral;
	}

	@Override
	public Class<SerializedValue> getAdaptedClass() {
		return SerializedValue.class;
	}

	@Override
	public Computation tryDeserialize(SerializedValue value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		TypeManager types = context.getTypes();
		Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
		if (value instanceof SerializedNull) {
			return expression("null", usedType);
		} else if (value instanceof SerializedLiteral) {
			return expression(asLiteral(((SerializedLiteral) value).getValue()), usedType);
		} else {
			return value.accept(generator);
		}
	}

}
