package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.iterables.IterableConditions.containingExactly;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.Methods;

public class ConfigurableTestRecorderAgentConfigTest {

	@Test
	public void testConfigurableTestRecorderAgentConfigBuilder() throws Exception {
		SnapshotConsumer consumer = new TestSnapshotConsumer();

		ConfigurableTestRecorderAgentConfig config = ConfigurableTestRecorderAgentConfig.builder(new DefaultTestRecorderAgentConfig())
			.withClasses(asList(Classes.byName("MyClass")))
			.withClassExclusions(asList(Classes.byName("NotMyClass")))
			.withFieldExclusions(asList(Fields.byName("notMyField")))
			.withGlobalFields(asList(Fields.byName("globalField")))
			.withInputs(asList(Methods.byName("inputMethod")))
			.withOutputs(asList(Methods.byName("outputMethod")))
			.withSnapshotConsumer(consumer)
			.withTimeoutInMillis(42)
			.build();

		assertThat(config.getClasses()).is(containingExactly(
			AClass.matching("MyClass")
				.andNotMatching("NotMyClass")));
		assertThat(config.getFieldExclusions()).is(containingExactly(
			AField.matching("anyClass", "notMyField", "any")
				.andNotMatching("anyClass", "myField", "any")));
		assertThat(config.getGlobalFields()).is(containingExactly(
			AField.matching("anyClass", "globalField", "any")
				.andNotMatching("anyClass", "notMyField", "any")));
		assertThat(config.getInputs()).is(containingExactly(
			AMethod.matching("anyClass", "inputMethod", "any")
				.andNotMatching("anyClass", "outputMethod", "any")));
		assertThat(config.getOutputs()).is(containingExactly(
			AMethod.matching("anyClass", "outputMethod", "any")
				.andNotMatching("anyClass", "inputMethod", "any")));
		assertThat(config.getSnapshotConsumer()).isSameAs(consumer);
		assertThat(config.getTimeoutInMillis()).isEqualTo(42);
	}
}
