package net.amygdalum.testrecorder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.amygdalum.testrecorder.profile.DefaultSerializationProfile;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Snapshot {
	Class<? extends SerializationProfile> profile() default DefaultSerializationProfile.class;
}
