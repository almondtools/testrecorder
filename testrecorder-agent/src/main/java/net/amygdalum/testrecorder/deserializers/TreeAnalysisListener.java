package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.types.SerializedAggregateType;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedStructuralType;
import net.amygdalum.testrecorder.types.SerializedValue;

public interface TreeAnalysisListener {

	void notifyThis(SerializedValue self);
	void notifyArgument(SerializedArgument argument);
	void notifyResult(SerializedResult result);
	void notifyException(SerializedValue exception);
	void notifyInput(SerializedValue in);
	void notifyOutput(SerializedValue out);
	void notifyGlobal(SerializedField global);
	void notifyField(SerializedStructuralType self, SerializedField field);
	void notifyAggregate(SerializedAggregateType self, SerializedValue value);
	void notifyReference(SerializedReferenceType self, SerializedValue value);
	
}