package net.amygdalum.testrecorder.types;

public interface SerializedRole {

    <T> T accept(RoleVisitor<T> visitor);

}
