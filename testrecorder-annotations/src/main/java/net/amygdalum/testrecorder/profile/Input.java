package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a method with {@link Input} specifies a method to be an input method. {@link Input} is inheritable, 
 * i.e. overriding methods in sub classes or implementation classes will also be handled as input. 
 * 
 * We define input as state that is dependent on sources not controlled by the JVM (e.g. filesystem, webservices,
 * random numbers, date/time).   
 * 
 * Note that a method could only be exclusively {@link Recorded}, {@link Input} or {@link Output}. {@link Input} 
 * will be ignored in presence of these annotations. 
 * 
 * @see "SerializationProfile#getInputs()"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Input {
}