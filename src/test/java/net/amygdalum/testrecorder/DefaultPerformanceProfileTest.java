package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultPerformanceProfileTest {

    private DefaultPerformanceProfile config;

    @BeforeEach
    public void before() throws Exception {
        config = new DefaultPerformanceProfile();
    }
    
    @Test
    public void testGetTimeoutInMillis() throws Exception {
        assertThat(config.getTimeoutInMillis()).isEqualTo(100_000l);
    }

}
