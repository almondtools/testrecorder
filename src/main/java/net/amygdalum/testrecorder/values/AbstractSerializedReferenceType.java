package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.SerializedReferenceType;

public abstract class AbstractSerializedReferenceType extends AbstractSerializedValue implements SerializedReferenceType {

    private int id;
    private Type resultType;

    public AbstractSerializedReferenceType(Type type) {
        super(type);
    }
    
    @Override
    public int getId() {
		return id;
	}
    
    @Override
    public void setId(int id) {
		this.id = id;
	}

    @Override
    public Type getResultType() {
        if (resultType == null) {
            return getType();
        } else {
            return resultType;
        }
    }

    @Override
    public void setResultType(Type resultType) {
        this.resultType = resultType;
    }

}
