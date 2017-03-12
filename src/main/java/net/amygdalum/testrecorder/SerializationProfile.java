package net.amygdalum.testrecorder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public interface SerializationProfile {

    boolean inherit();
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    public @interface Excluded {

    }

	List<Predicate<Field>> getFieldExclusions();

	List<Predicate<Class<?>>> getClassExclusions();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Global {
    }

	List<Field> getGlobalFields();
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @Repeatable(Hints.class)
    public @interface Hint {
        Class<? extends DeserializationHint> type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Hints {
        Hint[] value();
    }
    
    List<DeserializationHint> getHints();

}
