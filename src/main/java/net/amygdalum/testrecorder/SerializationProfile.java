package net.amygdalum.testrecorder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

public interface SerializationProfile {

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
	@Target({ElementType.METHOD})
	public @interface Output {
	}

    List<Method> getOutputMethods();
    
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Input {
	}

    List<Method> getInputMethods();
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface Hint {
        Class<? extends DeserializationHint> type();
    }

    List<DeserializationHint> getHints();
    
}
