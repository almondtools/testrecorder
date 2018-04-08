package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.ExcludeExplicitExcluded;
import net.amygdalum.testrecorder.profile.ExcludeGenerated;
import net.amygdalum.testrecorder.profile.ExcludeStatic;

public class DefaultSerializationProfileTest {

    @Test
    public void testConfig() throws Exception {
    	DefaultSerializationProfile config = new DefaultSerializationProfile();
        assertThat(config.getFieldExclusions()).hasOnlyElementsOfTypes(
        	ExcludeExplicitExcluded.class, 
        	ExcludeGenerated.class,
            ExcludeStatic.class);
        assertThat(config.getClassExclusions()).isEmpty();
        assertThat(config.getInputs()).isEmpty();
        assertThat(config.getOutputs()).isEmpty();
        assertThat(config.getGlobalFields()).isEmpty();
        assertThat(config.getClasses()).isEmpty();
    }

}
