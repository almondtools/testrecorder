package net.amygdalum.testrecorder.generator;

import java.util.List;

import net.amygdalum.testrecorder.deserializers.CustomAnnotation;

public interface TestGeneratorProfile {

	List<CustomAnnotation> annotations();
	
}
