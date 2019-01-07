package net.amygdalum.testrecorder.generator;

import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.deserializers.CustomAnnotation;

public class JUnit5TestGeneratorProfile implements TestGeneratorProfile {

	@Override
	public List<CustomAnnotation> annotations() {
		return emptyList();
	}

	@Override
	public Class<? extends TestTemplate> template() {
		return JUnit5TestTemplate.class;
	}

}