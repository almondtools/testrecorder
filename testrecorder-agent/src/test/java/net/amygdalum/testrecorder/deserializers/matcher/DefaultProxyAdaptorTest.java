package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.util.testobjects.NonGenericInterface;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedProxy;

public class DefaultProxyAdaptorTest {

	private AgentConfiguration config;
	private DefaultProxyAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultProxyAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAnyObject() throws Exception {
		assertThat(adaptor.matches(Proxy.getProxyClass(DefaultProxyAdaptorTest.class.getClassLoader(), NonGenericInterface.class))).isTrue();
		assertThat(adaptor.matches(Proxy.class)).isTrue();
		assertThat(adaptor.matches(Object.class)).isFalse();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		Class<?> clazz = Proxy.getProxyClass(DefaultProxyAdaptorTest.class.getClassLoader(), NonGenericInterface.class);
		SerializedProxy value = new SerializedProxy(clazz);
		value.useAs(NonGenericInterface.class);
		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*}.matching(Proxy.class, NonGenericInterface.class)");
	}
	
	@Test
	public void testTryDeserializeHidden() throws Exception {
		Class<?> clazz = Proxy.getProxyClass(DefaultProxyAdaptorTest.class.getClassLoader(), NonGenericInterface.class);
		SerializedProxy value = new SerializedProxy(clazz);
		value.useAs(Hidden.classOfCompletelyHidden());
		MatcherGenerators generator = generator();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*}.matching(Proxy.class)");
	}
	
	@Test
	public void testTryDeserializeMatcher() throws Exception {
		Class<?> clazz = Proxy.getProxyClass(DefaultProxyAdaptorTest.class.getClassLoader(), NonGenericInterface.class);
		SerializedProxy value = new SerializedProxy(clazz);
		value.useAs(Types.parameterized(Matcher.class, null, NonGenericInterface.class));
		MatcherGenerators generator = generator();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*}.matching(Proxy.class, NonGenericInterface.class)");
	}
	
	@Test
	public void testTryDeserializeProxyWithFields() throws Exception {
		Class<?> clazz = Proxy.getProxyClass(DefaultProxyAdaptorTest.class.getClassLoader(), NonGenericInterface.class);
		SerializedProxy value = new SerializedProxy(clazz);
		value.addField(new SerializedField(clazz, "str", String.class, SerializedLiteral.literal("strvalue")));
		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*String str = \"strvalue\";*}.matching(Proxy.class)");
	}

	private MatcherGenerators generator() {
		return new MatcherGenerators(new Adaptors<MatcherGenerators>(config).load(MatcherGenerator.class));
	}

}
