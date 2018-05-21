package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.serializers.DefaultMapSerializer;
import net.amygdalum.testrecorder.serializers.SerializerFacade;

public class CustomMapSerializer extends DefaultMapSerializer {

	public CustomMapSerializer(SerializerFacade facade) {
		super(facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(CustomMap.class);
	}

}
