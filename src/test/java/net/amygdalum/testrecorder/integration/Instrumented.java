package net.amygdalum.testrecorder.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Instrumented {

	Class<? extends SerializationProfile> config() default DefaultSerializationProfile.class;

	String[] classes();
	
	boolean serializeLambdas() default false;

}
