package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DeserializerContext {
    
    public static final DeserializerContext NULL = new DeserializerContext();
    
    private List<Object> hints;

    public DeserializerContext() {
        this.hints = new ArrayList<>();
    }
    
    public DeserializerContext(Collection<Object> hints) {
        this.hints = new ArrayList<>(hints);
    }

    public <T> Optional<T> getHint(Class<T> clazz) {
        return hints.stream()
            .filter(hint -> clazz.isInstance(hint))
            .map(hint -> clazz.cast(hint))
            .findFirst();
    }

    public <T> Stream<T> getHints(Class<T> clazz) {
        return hints.stream()
            .filter(hint -> clazz.isInstance(hint))
            .map(hint -> clazz.cast(hint));
    }

    @SafeVarargs
    public static <T extends Object> DeserializerContext newContext(T... objects) {
        return new DeserializerContext(asList(objects));
    }

}
