package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.SerializerFacade;

public class CustomListSerializer extends DefaultListSerializer {

	public CustomListSerializer(SerializerFacade facade) {
		super(facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(CustomList.class);
	}

}
