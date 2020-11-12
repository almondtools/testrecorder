package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.types.Serializer;

public class DefaultSerializers {
	public static List<Serializer<?>> defaults() {
		return asList(
			new ArraysListSerializer(),
			new CollectionsListSerializer(),
			new CollectionsSetSerializer(),
			new CollectionsMapSerializer(),
			new DefaultListSerializer(),
			new DefaultQueueSerializer(),
			new DefaultDequeSerializer(),
			new DefaultSetSerializer(),
			new DefaultMapSerializer(),
			new ClassSerializer(),
			new BigIntegerSerializer(),
			new BigDecimalSerializer());
	}
}
