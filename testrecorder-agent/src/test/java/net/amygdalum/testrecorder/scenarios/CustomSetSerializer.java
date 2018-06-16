package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.serializers.DefaultSetSerializer;

public class CustomSetSerializer extends DefaultSetSerializer {

	public CustomSetSerializer() {
		super();
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(CustomSet.class);
	}

}
