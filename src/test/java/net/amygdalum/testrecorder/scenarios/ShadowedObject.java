package net.amygdalum.testrecorder.scenarios;

public class ShadowedObject {

    private int field;
    private long longField;

    public ShadowedObject(int field, int longField) {
        this.field = field;
        this.longField = longField;
    }

    @Override
    public String toString() {
        return "" + field + longField;
    }
}
