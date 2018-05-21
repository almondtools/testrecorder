package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.serializers.DefaultSetSerializer;
import net.amygdalum.testrecorder.serializers.SerializerFacade;

public class CustomSetSerializer extends DefaultSetSerializer {

	public CustomSetSerializer(SerializerFacade facade) {
		super(facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(CustomSet.class);
	}

}
