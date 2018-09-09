package net.amygdalum.testrecorder.types;

import java.util.List;

public interface SerializedStructuralType extends SerializedReferenceType {

	List<SerializedField> fields();
	
}
