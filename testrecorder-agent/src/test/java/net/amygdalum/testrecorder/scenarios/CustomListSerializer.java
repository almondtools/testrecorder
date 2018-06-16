package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.serializers.DefaultListSerializer;

public class CustomListSerializer extends DefaultListSerializer {

	public CustomListSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(CustomList.class);
	}

}
