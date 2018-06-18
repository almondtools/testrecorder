package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.baseType;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Types;

public abstract class AbstractSerializedValue implements SerializedValue {

    private Class<?> type;

    public AbstractSerializedValue(Class<?> type) {
		assert type == null || type instanceof Serializable;
        this.type = type;
    }

    @Override
    public Type[] getUsedTypes() {
        return new Type[] {type};
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Annotation[] getAnnotations() {
        return type.getAnnotations();
    }

    @Override
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> clazz) {
        Annotation[] annotations = getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (clazz.isInstance(annotations[i])) {
                return Optional.of(clazz.cast(annotations[i]));
            }
        }
        return Optional.empty();
    }

	public Type inferType(Stream<Type> candidateTypes, Collection<SerializedValue> values, Class<?> defaultType) {
		return candidateTypes
			.filter(type -> satisfiesType(type, values))
			.sorted(Types::byMostConcreteGeneric)
			.findFirst()
			.orElse(defaultType);
	}

	public boolean satisfiesType(Type type, SerializedValue value) {
		Class<?> baseType = baseType(type);
		return value.getType() == null
			|| baseType.isAssignableFrom(baseType(value.getType()));
	}

	public boolean satisfiesType(Type type, Collection<? extends SerializedValue> collection) {
		return collection.stream()
			.allMatch(value -> satisfiesType(type, value));
	}


}
