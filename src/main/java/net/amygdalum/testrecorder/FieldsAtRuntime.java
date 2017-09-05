package net.amygdalum.testrecorder;

public interface FieldsAtRuntime extends Fields {

	default boolean matches(String className, String fieldName, String fieldDescriptor) {
		return false;
	}

}
