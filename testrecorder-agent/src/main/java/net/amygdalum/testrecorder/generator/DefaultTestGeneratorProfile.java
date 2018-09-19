package net.amygdalum.testrecorder.generator;

import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.deserializers.CustomAnnotation;

public class DefaultTestGeneratorProfile implements TestGeneratorProfile {

	public static final List<CustomAnnotation> DEFAULT_ANNOTATIONS = emptyList();

	@Override
	public List<CustomAnnotation> annotations() {
		return DEFAULT_ANNOTATIONS;
	}

}
