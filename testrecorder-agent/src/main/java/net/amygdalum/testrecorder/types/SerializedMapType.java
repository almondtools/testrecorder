package net.amygdalum.testrecorder.types;

import java.util.List;

public interface SerializedMapType extends SerializedReferenceType {

	List<SerializedKeyValue> elements();
	
}
