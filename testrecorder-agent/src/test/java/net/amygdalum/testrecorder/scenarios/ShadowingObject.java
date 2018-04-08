package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class ShadowingObject extends ShadowedObject {

    private String field;

    public ShadowingObject(String field) {
        super(42);
        this.field = field;
    }
    
    @Recorded
    public String getShadowingField() {
        return field;
    }
    
    @Override
    public String toString() {
        return getShadowingField() + " > " + getField();
    }

}
