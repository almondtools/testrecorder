package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.DeserializationHint;
import net.amygdalum.testrecorder.SerializedValue;

public abstract class AbstractSerializedValue implements SerializedValue {

	private Type type;
	private List<DeserializationHint> hints;

	public AbstractSerializedValue(Type type) {
		this.type = type;
	}

	@Override
	public Type getResultType() {
		return type;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public void addHints(List<DeserializationHint> hints) {
	    if (this.hints == null) {
	        this.hints = new ArrayList<>(hints);
	    } else {
	        this.hints.addAll(hints);
	    }
	}
	
	@Override
	public List<DeserializationHint> getHints() {
	    if (hints == null) {
	        return emptyList();
	    } else {
	    return hints;
	    }
	}
	
}
