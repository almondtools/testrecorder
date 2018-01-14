package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.*;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.Methods;

public class ConfigurableTestRecorderAgentConfigTest {

	@Test
	public void testConfigurableTestRecorderAgentConfigBuilder() throws Exception {
		ConfigurableTestRecorderAgentConfig config = ConfigurableTestRecorderAgentConfig.builder(new DefaultTestRecorderAgentConfig())
			.withClasses(asList(Classes.byName("MyClass")))
			.withClassExclusions(asList(Classes.byName("NotMyClass")))
			.withFieldExclusions(asList(Fields.byName("notMyField")))
			.withGlobalFields(asList(Fields.byName("globalField")))
			.withInputs(asList(Methods.byName("inputMethod")))
			.withOutputs(asList(Methods.byName("outputMethod")))
			.withSnapshotConsumer(new SnapshotConsumer() {

				@Override
				public void close() {
				}

				@Override
				public void accept(ContextSnapshot snapshot) {
				}
			})
			.withTimeoutInMillis(42)
			.build();
		assertThat(config.getClasses()).iterate()
			.next().satisfies(element -> {
				assertThat(element.matches("MyClass")).isTrue();
				assertThat(element.matches("NotMyClass")).isFalse();
			});
		assertThat(config.getClassExclusions()).iterate()
			.next().satisfies(element -> {
				assertThat(element.matches("NotMyClass")).isTrue();
				assertThat(element.matches("MyClass")).isFalse();
			});
		assertThat(config.getFieldExclusions()).iterate()
			.next().satisfies(element -> {
				assertThat(element.matches("anyClass", "notMyField", "any")).isTrue();
				assertThat(element.matches("anyClass", "myField", "any")).isFalse();
			});
		assertThat(config.getGlobalFields()).iterate()
			.next().satisfies(element -> {
				assertThat(element.matches("anyClass", "globalField", "any")).isTrue();
				assertThat(element.matches("anyClass", "notMyField", "any")).isFalse();
			});
		assertThat(config.getInputs()).iterate()
			.next().satisfies(element -> {
				assertThat(element.matches("anyClass", "inputMethod", "any")).isTrue();
				assertThat(element.matches("anyClass", "outputMethod", "any")).isFalse();
			});
		assertThat(config.getOutputs()).iterate()
			.next().satisfies(element -> {
				assertThat(element.matches("anyClass", "outputMethod", "any")).isTrue();
				assertThat(element.matches("anyClass", "inputMethod", "any")).isFalse();
			});
		assertThat(config.getSnapshotConsumer()).isNotNull();
		assertThat(config.getTimeoutInMillis()).isEqualTo(42);
	}

}
