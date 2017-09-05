package net.amygdalum.testrecorder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public interface SerializationProfile {

    boolean inherit();
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    public @interface Excluded {
    }

	List<Fields> getFieldExclusions();

	List<Classes> getClassExclusions();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Global {
    }

	List<Fields> getGlobalFields();
 
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Input {
	}
	
    List<Methods> getInputs();
    
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Output {
	}

    List<Methods> getOutputs();
    
}
