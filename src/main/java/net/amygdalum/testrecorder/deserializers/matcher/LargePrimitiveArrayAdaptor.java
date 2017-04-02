package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.equalToMatcher;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isLiteral;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.hints.LoadFromFile;
import net.amygdalum.testrecorder.util.FileSerializer;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class LargePrimitiveArrayAdaptor implements MatcherGenerator<SerializedArray> {

    @Override
    public Class<SerializedArray> getAdaptedClass() {
        return SerializedArray.class;
    }

    @Override
    public Class<? extends MatcherGenerator<SerializedArray>> parent() {
        return DefaultArrayAdaptor.class;
    }

    @Override
    public boolean matches(Type type) {
        return true;
    }

    @Override
    public Computation tryDeserialize(SerializedArray value, MatcherGenerators generator) throws DeserializationException {
        TypeManager types = generator.getTypes();
        Class<?> componentType = baseType(value.getComponentType());
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        if (isLiteral(componentType)) {
            Optional<LoadFromFile> hint = value.getHint(LoadFromFile.class);
            if (hint.isPresent()) {
                LoadFromFile loadFromFile = hint.get();
                types.staticImport(FileSerializer.class, "load");
                types.staticImport(Matchers.class, "equalTo");
                Object object = unwrap(value);
                String fileName = FileSerializer.store(loadFromFile.writeTo(), object);
                String result = callLocalMethod("load", asLiteral(loadFromFile.readFrom()), asLiteral(fileName), types.getRawTypeName(value.getType()));
                String matcher = equalToMatcher(result); 
                return new Computation(matcher, Matcher.class, new ArrayList<>());
            };
        }
        throw new DeserializationException(value.toString());
    }

    private Object unwrap(SerializedArray value) {
        SerializedValue[] serializedArray = value.getArray();
        Class<?> componentType = baseType(value.getComponentType());
        Object array = Array.newInstance(componentType, serializedArray.length);
        for (int i = 0; i < serializedArray.length; i++) {
            Array.set(array, i, unwrap(serializedArray[i]));
        }
        return array;
    }

    private Object unwrap(SerializedValue value) {
        if (value instanceof SerializedLiteral) {
            return ((SerializedLiteral) value).getValue();
        } else if (value instanceof SerializedArray) {
            return unwrap((SerializedArray) value);
        } else {
            throw new DeserializationException(value.toString());
        }
    }

}
