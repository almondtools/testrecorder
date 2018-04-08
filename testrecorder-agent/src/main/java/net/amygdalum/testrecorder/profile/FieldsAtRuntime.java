package net.amygdalum.testrecorder.profile;

public interface FieldsAtRuntime extends Fields {

	default boolean matches(String className, String fieldName, String fieldDescriptor) {
		return false;
	}

}
