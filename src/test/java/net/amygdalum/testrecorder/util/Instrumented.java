package net.amygdalum.testrecorder.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.TestRecorderAgentConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Instrumented {

	Class<? extends TestRecorderAgentConfig> config() default DefaultTestRecorderAgentConfig.class;

	String[] classes();

}
