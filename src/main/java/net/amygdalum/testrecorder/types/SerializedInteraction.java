package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.values.SerializedNull;

public interface SerializedInteraction {

	public static final SerializedNull VOID = nullInstance(void.class);

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