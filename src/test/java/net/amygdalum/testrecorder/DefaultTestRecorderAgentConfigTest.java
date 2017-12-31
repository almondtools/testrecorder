package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.ExcludeExplicitExcluded;
import net.amygdalum.testrecorder.profile.ExcludeGenerated;
import net.amygdalum.testrecorder.profile.ExcludeStatic;

public class DefaultTestRecorderAgentConfigTest {

    private DefaultTestRecorderAgentConfig config;

    @BeforeEach
    public void before() throws Exception {
        config = new DefaultTestRecorderAgentConfig();
    }
    
    @Test
    public void testGetFieldExclusions() throws Exception {
        assertThat(config.getFieldExclusions()).hasOnlyElementsOfTypes(
        	ExcludeExplicitExcluded.class, 
        	ExcludeGenerated.class,
            ExcludeStatic.class);
    }

    @Test
    public void testGetClassExclusions() throws Exception {
        assertThat(config.getClassExclusions()).isEmpty();
    }

    @Test
    public void testGetGlobalFields() throws Exception {
        assertThat(config.getGlobalFields()).isEmpty();
    }

    @Test
    public void testGetSnapshotConsumer() throws Exception {
        assertThat(config.getSnapshotConsumer()).isInstanceOf(TestGenerator.class);
    }

    @Test
    public void testGetPackages() throws Exception {
        assertThat(config.getClasses()).isEmpty();
    }

    @Test
    public void testGetTimeoutInMillis() throws Exception {
        assertThat(config.getTimeoutInMillis()).isEqualTo(100_000l);
    }

}
