package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a field or class with {@link Excluded} excludes it from serialization.
 * 
 * @see "SerializationProfile#getClassExclusions()"
 * @see "SerializationProfile#getFieldExclusions()"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface Excluded {
}