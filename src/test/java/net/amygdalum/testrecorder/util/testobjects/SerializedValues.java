package net.amygdalum.testrecorder.util.testobjects;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SerializedValues {
    
    private ConfigurableSerializerFacade facade;

    public SerializedValues() {
        facade = new ConfigurableSerializerFacade(new DefaultTestRecorderAgentConfig());
    }

    public SerializedList list(Type type, List<?> values) {
        DefaultListSerializer serializer = new DefaultListSerializer(facade);
        SerializedList value = serializer.generate(type, type);
        serializer.populate(value, values);
        return value;
    }
    
    public SerializedObject object(Type type, Object object) {
        GenericSerializer serializer = new GenericSerializer(facade);
        SerializedObject value = (SerializedObject) serializer.generate(type, object.getClass());
        serializer.populate(value, object);
        return value;
    }

    public SerializedImmutable<BigInteger> bigInteger(BigInteger object) {
        BigIntegerSerializer serializer = new BigIntegerSerializer(facade);
        SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, BigInteger.class);
        serializer.populate(value, object);
        return value;
    }

}
