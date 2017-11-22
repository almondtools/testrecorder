package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.SerializedValue;

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