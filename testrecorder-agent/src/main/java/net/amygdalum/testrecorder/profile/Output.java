package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a method with {@link Output} specifies a method to be an output method. {@link Output} is inheritable, 
 * i.e. overriding methods in sub classes or implementation classes will also be handled as output.
 * 
 * We define output as state that is consumed by systems not controlled by the JVM (e.g. filesystem, webservices, browser).
 * 
 * Note that a method could only be exclusively {@link Recorded}, {@link Input} or {@link Output}. {@link Output} 
 * will be ignored in presence of these annotations. 
 * 
 * @see SerializationProfile#getOutputs()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Output {
}