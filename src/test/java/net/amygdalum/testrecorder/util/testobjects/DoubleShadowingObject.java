package net.amygdalum.testrecorder.util.testobjects;

public class DoubleShadowingObject extends ShadowingObject {

    private String field;

    public DoubleShadowingObject(String field, String strfield, int superField) {
        super(strfield, superField);
        this.field = field;
    }

    public String getDoubleShadowingField() {
        return field;
    }

}
