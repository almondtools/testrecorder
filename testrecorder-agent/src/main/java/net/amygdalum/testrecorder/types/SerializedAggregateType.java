package net.amygdalum.testrecorder.types;

import java.util.List;

public interface SerializedAggregateType extends SerializedReferenceType {

	List<SerializedValue> elements();
	
}
