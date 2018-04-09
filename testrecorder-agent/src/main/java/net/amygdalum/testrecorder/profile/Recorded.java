package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.amygdalum.testrecorder.profile.SerializationProfile.Input;
import net.amygdalum.testrecorder.profile.SerializationProfile.Output;

/**
 * Annotating a method with {@link Recorded} specifies a method to be an recorded. 
 * 
 * {@link Recorded} has priority over annotations like {@link Input} or {@link Output}. 
 * The latter annotations will be ignored in presence of {@link Recorded}  
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Recorded {
}
