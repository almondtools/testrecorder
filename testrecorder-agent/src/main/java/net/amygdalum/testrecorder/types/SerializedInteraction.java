package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;
import java.util.List;

public interface SerializedInteraction {

	int STATIC = 0;
	
	int getId();
	
	boolean isComplete();

	Class<?> getDeclaringClass();

	String getMethodName();

	Type getResultType();

	SerializedResult getResult();

	Type[] getArgumentTypes();

	SerializedArgument[] getArguments();

	List<SerializedValue> getAllValues();

	boolean hasResult();

	boolean isStatic();

}