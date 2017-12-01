package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;
import java.util.List;

public interface SerializedInteraction {

	int getId();

	String getCallerClass();

	String getCallerMethod();

	int getCallerLine();
	
	Class<?> getDeclaringClass();

	String getName();

	Type getResultType();

	SerializedValue getResult();

	Type[] getTypes();

	SerializedValue[] getArguments();

	List<SerializedValue> getAllValues();

}