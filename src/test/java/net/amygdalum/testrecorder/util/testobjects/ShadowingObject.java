package net.amygdalum.testrecorder.util.testobjects;

public class ShadowingObject extends ShadowedObject {

    private String field;

    public ShadowingObject(String field, int superField) {
        super(superField);
        this.field = field;
    }

    public String getShadowingField() {
        return field;
    }

}
