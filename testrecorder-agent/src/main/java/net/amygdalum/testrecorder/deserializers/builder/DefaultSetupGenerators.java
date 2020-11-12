package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;

import java.util.List;

public class DefaultSetupGenerators {
	public static List<SetupGenerator<?>> defaults() {
		return asList(
			new DefaultLiteralAdaptor(),
			new DefaultNullAdaptor(),
			new DefaultClassAdaptor(),
			new DefaultBigIntegerAdaptor(),
			new DefaultBigDecimalAdaptor(),
			new DefaultEnumAdaptor(),
			new DefaultLambdaAdaptor(),
			new DefaultProxyAdaptor(),
			new ProxyPlaceholderAdaptor(),
			new ObjectBuilderAdaptor(),
			new ObjectFactoryAdaptor(),
			new BeanObjectAdaptor(),
			new DefaultObjectAdaptor(),
			new DefaultArrayAdaptor(),
			new ArraysListAdaptor(),
			new CollectionsListAdaptor(),
			new DefaultListAdaptor(),
			new DefaultQueueAdaptor(),
			new CollectionsSetAdaptor(),
			new DefaultSetAdaptor(),
			new CollectionsMapAdaptor(),
			new DefaultMapAdaptor(),
			new LargePrimitiveArrayAdaptor());
	}
}
