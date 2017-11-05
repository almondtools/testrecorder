package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenEnum;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class DefaultEnumAdaptorTest {

	private static final DeserializerContext ctx = DeserializerContext.NULL;

	private DefaultEnumAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultEnumAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesOnlyEnums() throws Exception {
		assertThat(adaptor.matches(PublicEnum.class), is(true));
		assertThat(adaptor.matches(classOfHiddenEnum()), is(true));
		assertThat(adaptor.matches(Enum.class), is(false));
		assertThat(adaptor.matches(Object.class), is(false));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedEnum value = new SerializedEnum(PublicEnum.class);
		value.setName("VALUE1");
		MatcherGenerators generator = new MatcherGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, ctx);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("sameInstance(PublicEnum.VALUE1)"));
	}

	@Test
	public void testTryDeserializeHidden() throws Exception {
		SerializedEnum value = new SerializedEnum(classOfHiddenEnum());
		value.setName("VALUE2");
		MatcherGenerators generator = new MatcherGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, ctx);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("matchingEnum(\"VALUE2\")"));
	}

	@Test
	public void testTryDeserializeHiddenWithGenericResultType() throws Exception {
		SerializedEnum value = new SerializedEnum(classOfHiddenEnum()).withResult(Object.class);
		value.setName("VALUE2");
		MatcherGenerators generator = new MatcherGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, ctx);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("widening(matchingEnum(\"VALUE2\"))"));
	}

}
