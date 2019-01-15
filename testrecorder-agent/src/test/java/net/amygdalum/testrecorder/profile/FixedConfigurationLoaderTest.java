package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;

public class FixedConfigurationLoaderTest {

	@Nested
	class testLoad {
		@Test
		void noProvidedValue() throws Exception {
			FixedConfigurationLoader loader = new FixedConfigurationLoader();

			assertThat(loader.load(Interface.class).findFirst()).isNotPresent();
		}

		@Test
		void withValue() throws Exception {
			FixedConfigurationLoader loader = new FixedConfigurationLoader().provide(Interface.class, new Implementation());

			assertThat(loader.load(Interface.class).findFirst()).containsInstanceOf(Implementation.class);
		}

		@Test
		void withFunction() throws Exception {
			FixedConfigurationLoader loader = new FixedConfigurationLoader().provide(Interface.class, args -> new ImplementationWithArgs(args));

			assertThat(loader.load(Interface.class, "arg1", "arg2").findFirst())
				.containsInstanceOf(ImplementationWithArgs.class)
				.map(result -> ((ImplementationWithArgs) result).getArgs())
				.hasValue(new Object[] {"arg1", "arg2"});
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		void withBrokenFunction(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
			FixedConfigurationLoader loader = new FixedConfigurationLoader().provide(Interface.class, args -> new BrokenImplementation());

			assertThat(loader.load(Interface.class).findFirst()).isNotPresent();
			assertThat(error.toString()).contains("failed to provide Interface");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		void withWronglyTypedFunction(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
			FixedConfigurationLoader loader = new FixedConfigurationLoader().provide(Interface.class, args -> new NoImplementation());

			assertThat(loader.load(Interface.class).findFirst()).isNotPresent();
			assertThat(error.toString()).contains("loaded class NoImplementation is not a subclass of Interface");
		}
	}

	interface Interface {

	}

	private class Implementation implements Interface {

	}

	private class NoImplementation {
	}

	private class BrokenImplementation implements Interface {
		BrokenImplementation() {
			throw new RuntimeException();
		}
	}

	private class ImplementationWithArgs implements Interface {
		private Object[] args;

		ImplementationWithArgs(Object[] args) {
			this.args = args;
		}

		public Object[] getArgs() {
			return args;
		}
	}
}
