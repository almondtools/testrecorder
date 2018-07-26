package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ConfigurableSerializationProfileTest {

	@Test
	void testBuilder() throws Exception {
		ConfigurableSerializationProfile profile = ConfigurableSerializationProfile.builder()
			.withClasses(Classes.byName("Class"))
			.withRecorded(Methods.byName("recorded"))
			.withClassExclusions(Classes.byName("ClassExcluded"))
			.withClassFacades(Classes.byName("ClassFacaded"))
			.withFieldExclusions(Fields.byName("fieldExcluded"))
			.withFieldFacades(Fields.byName("fieldFacaded"))
			.withGlobalFields(Fields.byName("global"))
			.withInputs(Methods.byName("input"))
			.withOutputs(Methods.byName("output"))
			.build();

		assertThat(profile.getClasses())
			.allMatch(c -> c.matches("Class"))
			.noneMatch(c -> c.matches("AnyOther"));
		assertThat(profile.getRecorded())
			.allMatch(m -> m.matches("AnyName", "recorded", "Any()"))
			.noneMatch(m -> m.matches("AnyName", "other", "Any()"));
		assertThat(profile.getClassExclusions())
			.allMatch(c -> c.matches("ClassExcluded"))
			.noneMatch(c -> c.matches("AnyOther"));
		assertThat(profile.getClassFacades())
			.allMatch(c -> c.matches("ClassFacaded"))
			.noneMatch(c -> c.matches("AnyOther"));
		assertThat(profile.getFieldExclusions())
			.allMatch(f -> f.matches("AnyClass", "fieldExcluded", "Any"))
			.noneMatch(f -> f.matches("AnyClass", "otherField", "Any"));
		assertThat(profile.getFieldFacades())
			.allMatch(f -> f.matches("AnyClass", "fieldFacaded", "Any"))
			.noneMatch(f -> f.matches("AnyClass", "otherField", "Any"));
		assertThat(profile.getGlobalFields())
			.allMatch(f -> f.matches("AnyClass", "global", "Any"))
			.noneMatch(f -> f.matches("AnyClass", "otherField", "Any"));
		assertThat(profile.getInputs())
			.allMatch(m -> m.matches("AnyName", "input", "Any()"))
			.noneMatch(m -> m.matches("AnyName", "other", "Any()"));
		assertThat(profile.getOutputs())
			.allMatch(m -> m.matches("AnyName", "output", "Any()"))
			.noneMatch(m -> m.matches("AnyName", "other", "Any()"));
	}

	@Test
	void testBuilderWithBaseProfile() throws Exception {
		ConfigurableSerializationProfile baseprofile = ConfigurableSerializationProfile.builder()
			.withClasses(Classes.byName("Class"))
			.withRecorded(Methods.byName("recorded"))
			.withClassExclusions(Classes.byName("ClassExcluded"))
			.withClassFacades(Classes.byName("ClassFacaded"))
			.withFieldExclusions(Fields.byName("fieldExcluded"))
			.withFieldFacades(Fields.byName("fieldFacaded"))
			.withGlobalFields(Fields.byName("global"))
			.withInputs(Methods.byName("input"))
			.withOutputs(Methods.byName("output"))
			.build();
		
		ConfigurableSerializationProfile profile = ConfigurableSerializationProfile.builder(baseprofile).build();

		assertThat(profile.getClasses())
			.allMatch(c -> c.matches("Class"))
			.noneMatch(c -> c.matches("AnyOther"));
		assertThat(profile.getRecorded())
			.allMatch(m -> m.matches("AnyName", "recorded", "Any()"))
			.noneMatch(m -> m.matches("AnyName", "other", "Any()"));
		assertThat(profile.getClassExclusions())
			.allMatch(c -> c.matches("ClassExcluded"))
			.noneMatch(c -> c.matches("AnyOther"));
		assertThat(profile.getClassFacades())
			.allMatch(c -> c.matches("ClassFacaded"))
			.noneMatch(c -> c.matches("AnyOther"));
		assertThat(profile.getFieldExclusions())
			.allMatch(f -> f.matches("AnyClass", "fieldExcluded", "Any"))
			.noneMatch(f -> f.matches("AnyClass", "otherField", "Any"));
		assertThat(profile.getFieldFacades())
			.allMatch(f -> f.matches("AnyClass", "fieldFacaded", "Any"))
			.noneMatch(f -> f.matches("AnyClass", "otherField", "Any"));
		assertThat(profile.getGlobalFields())
			.allMatch(f -> f.matches("AnyClass", "global", "Any"))
			.noneMatch(f -> f.matches("AnyClass", "otherField", "Any"));
		assertThat(profile.getInputs())
			.allMatch(m -> m.matches("AnyName", "input", "Any()"))
			.noneMatch(m -> m.matches("AnyName", "other", "Any()"));
		assertThat(profile.getOutputs())
			.allMatch(m -> m.matches("AnyName", "output", "Any()"))
			.noneMatch(m -> m.matches("AnyName", "other", "Any()"));
	}

}
