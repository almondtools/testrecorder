package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.serializers.DefaultMapSerializer;

public class CustomMapSerializer extends DefaultMapSerializer {

	public CustomMapSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(CustomMap.class);
	}

}
