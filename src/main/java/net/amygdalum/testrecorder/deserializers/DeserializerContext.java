package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DeserializerContext {
    
    public static final DeserializerContext NULL = new DeserializerContext();
    
    private List<Annotation> annotations;

    public DeserializerContext() {
        this.annotations = new ArrayList<>();
    }
    
    public DeserializerContext(Collection<Annotation> annotations) {
        this.annotations = new ArrayList<>(annotations);
    }

    public <T extends Annotation> Optional<T> getHint(Class<T> clazz) {
        return annotations.stream()
            .filter(annotation -> clazz.isInstance(annotation))
            .map(annotation -> clazz.cast(annotation))
            .findFirst();
    }

    public static DeserializerContext newContext(Annotation... annotations) {
        return new DeserializerContext(asList(annotations));
    }

}
