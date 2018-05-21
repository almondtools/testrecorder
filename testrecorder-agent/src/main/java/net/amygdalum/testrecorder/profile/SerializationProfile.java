package net.amygdalum.testrecorder.profile;

import java.util.List;

public interface SerializationProfile {

	List<Classes> getClasses();

	/**
	 * Configuring {@link #getFieldExclusions()} enables excluding specified fields from serialization. Exclude fields
	 * from serializationif they are irrelevant or not easily serialized.
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Fields} provides some default predicates to put into this list.
	 *  
	 * @return a list of Fields/Predicates describing the fields to be excluded.
	 */
	List<Fields> getFieldExclusions();

	/**
	 * Configuring {@link #getFieldFacades()} enables facading specified fields at serialization. Facading means that a stub of
	 * the instance is recorded, but the internal state is skipped. Facade fields if their internal state is not relevant
	 * (yet their behavior may be).
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Fields} provides some default predicates to put into this list.
	 *  
	 * @return a list of Fields/Predicates describing the fields to be facaded.
	 */
	List<Fields> getFieldFacades();

	/**
	 * Configuring {@link #getClassExclusions()} enables excluding specified classes from serialization. Exclude classes
	 * from serialization if they are irrelevant or not easily serialized.
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Classes} provides some default predicates to put into this list.
	 *  
	 * @return a list of Classes/Predicates describing the classes to be excluded.
	 */
	List<Classes> getClassExclusions();

	/**
	 * Configuring {@link #getClassFacades()} enables facading specified classes at serialization. Facading means that a stub of
	 * the instance is recorded, but the internal state is skipped. Facade classes if their internal state is not relevant 
	 * (yet their behavior may be).
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Classes} provides some default predicates to put into this list.
	 *  
	 * @return a list of Classes/Predicates describing the classes to be facaded.
	 */
	List<Classes> getClassFacades();
	
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
	 * Configuring {@link #getOutputs()} enables to extend serialization by output methods.
	 * 
	 * We define output as state that is consumed by systems not controlled by the JVM (e.g. filesystem, webservices, browser).
	 * Methods that consume such output as arguments can be specified as output methods by adding a specification to this list.   
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Methods} provides some default predicates to specify methods that provide input.
	 *  
	 * @see Output
	 * @return a list of Methods/Predicates describing the methods consuming output.
	 */
	List<Methods> getOutputs();

	/**
	 * Configuring {@link #getRecorded()} enables to extend serialization by recorded methods.
	 * 
	 * {@link net.amygdalum.testrecorder.profile.Methods} provides some default predicates to specify methods that provide input.
	 *  
	 * @see Recorded
	 * @return a list of Methods/Predicates describing the methods that should be recorded.
	 */
	List<Methods> getRecorded();

}
