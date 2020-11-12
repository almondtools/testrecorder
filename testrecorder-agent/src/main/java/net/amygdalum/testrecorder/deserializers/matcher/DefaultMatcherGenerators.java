package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Arrays.asList;

import java.util.List;

public class DefaultMatcherGenerators {
	public static List<MatcherGenerator<?>> defaults() {
		return asList(
			new DefaultLiteralAdaptor(),
			new DefaultNullAdaptor(),
			new DefaultClassAdaptor(),
			new DefaultBigIntegerAdaptor(),
			new DefaultBigDecimalAdaptor(),
			new DefaultEnumAdaptor(),
			new DefaultLambdaAdaptor(),
			new DefaultProxyAdaptor(),
			new DefaultObjectAdaptor(),
			new DefaultArrayAdaptor(),
			new DefaultSequenceAdaptor(),
			new DefaultSetAdaptor(),
			new DefaultMapAdaptor(),
			new LargePrimitiveArrayAdaptor()
);
	}
}
