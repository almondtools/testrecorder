package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptor extends DefaultGenericMapAdaptor<SerializedMap> implements SetupGenerator<SerializedMap> {

    @Override
    public Class<SerializedMap> getAdaptedClass() {
        return SerializedMap.class;
    }

    @Override
    public Class<?>[] matchingTypes() {
        return  new Class[]{Map.class};
    }

    @Override
    public Type keyType(SerializedMap value) {
        return value.getMapKeyType();
    }
    
    @Override
    public Type valueType(SerializedMap value) {
        return value.getMapValueType();
    }
    
    @Override
    public Stream<Pair<SerializedValue,SerializedValue>> entries(SerializedMap value) {
        return value.entrySet().stream()
            .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }

}
