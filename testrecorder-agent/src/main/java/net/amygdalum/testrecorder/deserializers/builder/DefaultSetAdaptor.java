package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptor extends DefaultGenericCollectionAdaptor<SerializedSet> implements SetupGenerator<SerializedSet> {

    @Override
    public Class<SerializedSet> getAdaptedClass() {
        return SerializedSet.class;
    }

    @Override
    public Class<?>[] matchingTypes() {
        return  new Class[]{Set.class};
    }

    @Override
    public Type componentType(SerializedSet value) {
        return value.getComponentType();
    }
    
    @Override
    public Stream<SerializedValue> elements(SerializedSet value) {
        return value.stream();
    }

}
