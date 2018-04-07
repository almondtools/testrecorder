package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class Other {

    public static class ShadowingObject extends net.amygdalum.testrecorder.scenarios.ShadowingObject {

        private int field;

        public ShadowingObject(int field) {
            super("field");
            this.field = field;
        }
        
        @Recorded
        public int getDoubleShadowingField() {
            return field;
        }

        @Override
        public String toString() {
            return getDoubleShadowingField() + " > " + super.toString();
        }

    }
}
