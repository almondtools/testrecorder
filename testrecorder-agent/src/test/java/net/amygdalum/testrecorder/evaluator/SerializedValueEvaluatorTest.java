package net.amygdalum.testrecorder.evaluator;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class SerializedValueEvaluatorTest {

	private ConfigurableSerializerFacade facade;
	private SerializerSession session;

	@BeforeEach
	public void before() throws Exception {
		AgentConfiguration config = defaultConfig();
		facade = new ConfigurableSerializerFacade(config);
		session = facade.newSession();
	}

	@Nested
	class testParsing {

		@Test
		public void success() throws Exception {
			SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"), session);

			assertThat(new SerializedValueEvaluator("").applyTo(value)).contains(value);
			assertThat(new SerializedValueEvaluator("").error()).isEmpty();
			assertThat(new SerializedValueEvaluator(".str").applyTo(value)).contains(literal("strValue"));
			assertThat(new SerializedValueEvaluator(".str").error()).isEmpty();
		}

		@Test
		public void fails() throws Exception {
			SerializedValue value = facade.serialize(String.class, "str", session);

			assertThat(new SerializedValueEvaluator("str").applyTo(value).isPresent()).isFalse();
			assertThat(new SerializedValueEvaluator("str").error().map(e -> e.getMessage()).orElse(null)).isEqualTo("expecting '.' or '[', but found: 'str'");
			assertThat(new SerializedValueEvaluator("[").applyTo(value).isPresent()).isFalse();
			assertThat(new SerializedValueEvaluator("[").error().map(e -> e.getMessage()).orElse(null)).isEqualTo("expecting <index>, but nothing found");
			assertThat(new SerializedValueEvaluator("[1").applyTo(value).isPresent()).isFalse();
			assertThat(new SerializedValueEvaluator("[1").error().map(e -> e.getMessage()).orElse(null)).isEqualTo("expecting ']', but nothing found");
			assertThat(new SerializedValueEvaluator("[a").applyTo(value).isPresent()).isFalse();
			assertThat(new SerializedValueEvaluator("[a").error().map(e -> e.getMessage()).orElse(null)).isEqualTo("expecting <index>, but found: 'a'");
			assertThat(new SerializedValueEvaluator("[1.").applyTo(value).isPresent()).isFalse();
			assertThat(new SerializedValueEvaluator("[1.").error().map(e -> e.getMessage()).orElse(null)).isEqualTo("expecting ']', but found: '.'");
		}

	}

	@Nested
	class testEvaluatingPathOn {

		@Nested
		class Literals {
			@Test
			public void fails() throws Exception {
				SerializedValue value = facade.serialize(String.class, "str", session);

				assertThat(new SerializedValueEvaluator(".str").applyTo(value).isPresent()).isFalse();
				assertThat(new SerializedValueEvaluator("[0]").applyTo(value).isPresent()).isFalse();
			}
		}

		@Nested
		class Fields {

			@Test
			public void success() throws Exception {
				SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"), session);

				assertThat(new SerializedValueEvaluator(".str").applyTo(value).get().toString()).isEqualTo("strValue");
			}

			@Test
			public void fails() throws Exception {
				SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"), session);
				SerializedValue nullValue = facade.serialize(Simple.class, null, session);

				assertThat(new SerializedValueEvaluator(".s").applyTo(value).isPresent()).isFalse();
				assertThat(new SerializedValueEvaluator(".str").applyTo(nullValue).isPresent()).isFalse();
			}
		}

		@Nested
		class Arrays {

			@Test
			public void success() throws Exception {
				SerializedValue value = facade.serialize(String[].class, new String[] {"foo", "bar"}, session);

				assertThat(new SerializedValueEvaluator("[0]").applyTo(value).get().toString()).isEqualTo("foo");
				assertThat(new SerializedValueEvaluator("[1]").applyTo(value).get().toString()).isEqualTo("bar");
			}

			@Test
			public void fails() throws Exception {
				SerializedValue value = facade.serialize(String[].class, new String[] {"foo", "bar"}, session);

				assertThat(new SerializedValueEvaluator("[2]").applyTo(value).isPresent()).isFalse();
				assertThat(new SerializedValueEvaluator("[-1]").applyTo(value).isPresent()).isFalse();
				assertThat(new SerializedValueEvaluator("[str]").applyTo(value).isPresent()).isFalse();
			}
		}

		@Nested
		class Lists {

			@Test
			public void success() throws Exception {
				SerializedValue value = facade.serialize(List.class, asList("bar", "foo"), session);

				assertThat(new SerializedValueEvaluator("[0]").applyTo(value).get().toString()).isEqualTo("bar");
				assertThat(new SerializedValueEvaluator("[1]").applyTo(value).get().toString()).isEqualTo("foo");
			}

			@Test
			public void fails() throws Exception {
				SerializedValue value = facade.serialize(List.class, asList("bar", "foo"), session);

				assertThat(new SerializedValueEvaluator("[2]").applyTo(value).isPresent()).isFalse();
				assertThat(new SerializedValueEvaluator("[-1]").applyTo(value).isPresent()).isFalse();
				assertThat(new SerializedValueEvaluator("[str]").applyTo(value).isPresent()).isFalse();
			}

		}

	}

	@Nested
	class testEvaluatingLongPath {
		@Test
		public void success() throws Exception {
			SerializedValue value = facade.serialize(Complex.class, new Complex("sstr"), session);

			assertThat(new SerializedValueEvaluator(".simple.str").applyTo(value).get().toString()).isEqualTo("sstr");
		}

	}

	@Nested
	class testEvaluatingWithTypeCheck {
		@Test
		public void success() throws Exception {
			SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"), session);

			assertThat(new SerializedValueEvaluator("", Simple.class).applyTo(value)).contains(value);
			assertThat(new SerializedValueEvaluator(".str", String.class).applyTo(value)).contains(literal("strValue"));
		}

		@Test
		public void fails() throws Exception {
			SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"), session);

			assertThat(new SerializedValueEvaluator("", String.class).applyTo(value)).isEmpty();
			assertThat(new SerializedValueEvaluator(".str", Integer.class).applyTo(value)).isEmpty();
		}

	}

}
