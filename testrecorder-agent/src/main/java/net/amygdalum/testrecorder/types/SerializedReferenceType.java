package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;

public interface SerializedReferenceType extends SerializedValue {

	void setId(int id);

	int getId();

	void useAs(Type type);

}
