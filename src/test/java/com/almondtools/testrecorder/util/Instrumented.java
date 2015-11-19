package com.almondtools.testrecorder.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Instrumented {

	Class<? extends SnapshotConfig> config() default DefaultConfig.class;

	String[] classes();

}
