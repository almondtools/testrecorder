package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;
import java.util.List;

public interface SerializedInteraction {

	int getId();
	
	boolean isComplete();

	Class<?> getDeclaringClass();

	String getName();

	Type getResultType();

	SerializedValue getResult();

	Type[] getTypes();

	SerializedValue[] getArguments();

	List<SerializedValue> getAllValues();

	boolean hasResult();

}