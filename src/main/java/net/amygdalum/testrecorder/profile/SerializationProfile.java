package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public interface SerializationProfile {

	long getTimeoutInMillis();

	/**
	 * Annotating a field or class with {@link Excluded} excludes it from serialization.
	 * 
	 * @see SerializationProfile#getClassExclusions()
	 * @see SerializationProfile#getFieldExclusions()
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
	public @interface Excluded {
	}

	/**
	 * Configuring {@link #getFieldExclusions()} enables excluding specified fields from serialization 
	 * (e.g if they are irrelevant or not easily serialized).
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Fields} provides some default predicates to put into this list.
	 *  
	 * @return a list of Fields/Predicates describing the fields to be excluded.
	 */
	List<Fields> getFieldExclusions();

	/**
	 * Configuring {@link #getClassExclusions()} enables excluding specified classes from serialization 
	 * (e.g if they are irrelevant or not easily serialized).
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Classes} provides some default predicates to put into this list.
	 *  
	 * @return a list of Classes/Predicates describing the classes to be excluded.
	 */
	List<Classes> getClassExclusions();

	/**
	 * Annotating a field with {@link Global} specifies a field to be serialized as global variable.
	 * 
	 * @see SerializationProfile#getGlobalFields()
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface Global {
	}

	/**
	 * Configuring {@link #getGlobalFields()} enables to extend serialization by global/static fields. Global/Static
	 * fields are not tracked by default and must be added to the configuration if needed. 
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Fields} provides some default predicates to specify global variables/static fields.
	 *  
	 * @see Global
	 * @return a list of Fields/Predicates describing the fields to be serialized as global variables.
	 */
	List<Fields> getGlobalFields();

	/**
	 * Annotating a method with {@link Input} specifies a method to be an input method.
	 * 
	 * We define input as state that is dependent on sources not controlled by the JVM (e.g. filesystem, webservices,
	 * random numbers, date/time).   
	 * 
	 * Note that the Input annotation is a bit tricky yet. Input is recognized at the callers site (not at the site of the called method).
	 * Consequently it is not sufficient to annotate all implementations of the method, but in general also all abstract methods
	 * (including interface definitions).
	 * 
	 * @see SerializationProfile#getInputs()
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Input {
	}

	/**
	 * Configuring {@link #getInputs()} enables to extend serialization by input methods.
	 * 
	 * We define input as state that is dependent on sources not controlled by the JVM (e.g. filesystem, webservices,
	 * random numbers, date/time). Methods that provide such input as arguments or results can be specified as input methods
	 * by adding a specification to this list.   
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Methods} provides some default predicates to specify methods that provide input.
	 *  
	 * @see Input
	 * @return a list of Methods/Predicates describing the methods providing input.
	 */
	List<Methods> getInputs();

	/**
	 * Annotating a method with {@link Output} specifies a method to be an output method.
	 * 
	 * We define output as state that is consumed by systems not controlled by the JVM (e.g. filesystem, webservices, browser).
	 * 
	 * Output is recognized at the callers site (not at the site of the called method).
	 * Consequently it is not sufficient to include specifications for all implementations of the method, but in general also all abstract methods
	 * (including interface definitions).
	 * 
	 * Note that the Output annotation is a bit tricky yet. Output is recognized at the callers site (not at the site of the called method).
	 * Consequently it is not sufficient to annotate all implementations of the method, but in general also all abstract methods
	 * (including interface definitions).
	 * 
	 * @see SerializationProfile#getOutputs()
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Output {
	}

	/**
	 * Configuring {@link #getOutputs()} enables to extend serialization by output methods.
	 * 
	 * We define output as state that is consumed by systems not controlled by the JVM (e.g. filesystem, webservices, browser).
	 * Methods that consume such output as arguments can be specified as output methods by adding a specification to this list.   
	 * 
	 * Input is recognized at the callers site (not at the site of the called method).
	 * Consequently it is not sufficient to include specifications for all implementations of the method, but in general also all abstract methods
	 * (including interface definitions).
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Methods} provides some default predicates to specify methods that provide input.
	 *  
	 * @see Output
	 * @return a list of Methods/Predicates describing the methods consuming output.
	 */
	List<Methods> getOutputs();

}
