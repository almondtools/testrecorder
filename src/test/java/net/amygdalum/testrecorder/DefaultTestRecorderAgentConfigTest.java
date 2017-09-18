package net.amygdalum.testrecorder;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.profile.ExcludeExplicitExcluded;
import net.amygdalum.testrecorder.profile.ExcludeGenerated;
import net.amygdalum.testrecorder.profile.ExcludeStatic;

public class DefaultTestRecorderAgentConfigTest {

    private DefaultTestRecorderAgentConfig config;

    @Before
    public void before() throws Exception {
        config = new DefaultTestRecorderAgentConfig();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetFieldExclusions() throws Exception {
        assertThat(config.getFieldExclusions(), containsInAnyOrder(
            instanceOf(ExcludeExplicitExcluded.class),
            instanceOf(ExcludeGenerated.class),
            instanceOf(ExcludeStatic.class)
            ));
    }

    @Test
    public void testGetClassExclusions() throws Exception {
        assertThat(config.getClassExclusions(), empty());
    }

    @Test
    public void testGetGlobalFields() throws Exception {
        assertThat(config.getGlobalFields(), empty());
    }

    @Test
    public void testGetSnapshotConsumer() throws Exception {
        assertThat(config.getSnapshotConsumer(), instanceOf(TestGenerator.class));
    }

    @Test
    public void testGetPackages() throws Exception {
        assertThat(config.getPackages(), empty());
    }

    @Test
    public void testGetTimeoutInMillis() throws Exception {
        assertThat(config.getTimeoutInMillis(), equalTo(100_000l));
    }

}
