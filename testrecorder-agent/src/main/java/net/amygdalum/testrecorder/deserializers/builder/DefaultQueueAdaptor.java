package net.amygdalum.testrecorder.deserializers.builder;

import java.lang.reflect.Type;
import java.util.Queue;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultQueueAdaptor extends DefaultGenericCollectionAdaptor<SerializedList> implements SetupGenerator<SerializedList> {

    @Override
    public Class<SerializedList> getAdaptedClass() {
        return SerializedList.class;
    }

    @Override
    public Class<?>[] matchingTypes() {
        return  new Class[]{Queue.class};
    }

    @Override
    public Type componentType(SerializedList value) {
        return value.getComponentType();
    }
    
    @Override
    public Stream<SerializedValue> elements(SerializedList value) {
        return value.stream();
    }

}
