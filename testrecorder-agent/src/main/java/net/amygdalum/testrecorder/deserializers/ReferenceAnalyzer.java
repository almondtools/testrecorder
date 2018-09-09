package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.types.SerializedAggregateType;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedKeyValue;
import net.amygdalum.testrecorder.types.SerializedMapType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedStructuralType;
import net.amygdalum.testrecorder.types.SerializedValue;

public class ReferenceAnalyzer implements TreeAnalysisListener {

	private DefaultDeserializerContext context;
	
	public ReferenceAnalyzer(DefaultDeserializerContext context) {
		this.context = context;
	}
	
	@Override
	public void notifyThis(SerializedValue self) {
		staticReference(self);
	}

	@Override
	public void notifyArgument(SerializedArgument argument) {
		staticReference(argument.getValue());
	}

	@Override
	public void notifyResult(SerializedResult result) {
		staticReference(result.getValue());
	}

	@Override
	public void notifyException(SerializedValue exception) {
		staticReference(exception);
	}

	@Override
	public void notifyInput(SerializedValue in) {
		staticReference(in);
	}

	@Override
	public void notifyOutput(SerializedValue out) {
		staticReference(out);
	}

	@Override
	public void notifyGlobal(SerializedField global) {
		staticReference(global.getValue());
	}

	@Override
	public void notifyField(SerializedStructuralType self, SerializedField field) {
		reference(self, field.getValue());
	}

	@Override
	public void notifyAggregate(SerializedAggregateType self, SerializedValue value) {
		reference(self, value);
	}

	@Override
	public void notifyMap(SerializedMapType self, SerializedKeyValue keyvalue) {
		reference(self, keyvalue.getKey());
		reference(self, keyvalue.getValue());
	}

	@Override
	public void notifyReference(SerializedReferenceType self, SerializedValue value) {
		reference(self, value);
	}

	private void staticReference(SerializedValue value) {
		context.staticRef(value);
	}

	public void reference(SerializedReferenceType object, SerializedValue value) {
		context.ref(object, value);
	}


}