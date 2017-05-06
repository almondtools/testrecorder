package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;

public class ShadowingObject extends ShadowedObject {

    private String field;
    private long longField;

    
    public ShadowingObject(String field, int longField) {
        super(longField, longField);
        this.field = field;
        this.longField = longField;
    }

    @Recorded
    @Override
    public String toString() {
        return field + longField;
    }
}
