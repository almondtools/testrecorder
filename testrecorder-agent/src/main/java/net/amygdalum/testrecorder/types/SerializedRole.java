package net.amygdalum.testrecorder.types;

import java.lang.annotation.Annotation;

public interface SerializedRole {

    public static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    <T> T accept(RoleVisitor<T> visitor);

    Annotation[] getAnnotations();

}
