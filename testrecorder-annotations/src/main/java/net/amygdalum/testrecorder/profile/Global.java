package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a field with {@link Global} specifies a field to be serialized as global variable.
 * 
 * @see "SerializationProfile#getGlobalFields()"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Global {
}