package net.amygdalum.testrecorder.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.amygdalum.testrecorder.DefaultConfig;
import net.amygdalum.testrecorder.SnapshotConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Instrumented {

	Class<? extends SnapshotConfig> config() default DefaultConfig.class;

	String[] classes();

}
