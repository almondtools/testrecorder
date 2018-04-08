package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DefaultPerformanceProfileTest {

	@Test
    public void testConfig() throws Exception {
    	DefaultPerformanceProfile config = new DefaultPerformanceProfile();
        assertThat(config.getTimeoutInMillis()).isEqualTo(100_000l);
        assertThat(config.getIdleTime()).isEqualTo(10_000l);
    }

}
