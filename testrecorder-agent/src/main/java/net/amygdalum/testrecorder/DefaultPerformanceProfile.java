package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.PerformanceProfile;

public class DefaultPerformanceProfile implements PerformanceProfile {

    @Override
    public long getTimeoutInMillis() {
        return 100_000;
    }

	@Override
	public long getIdleTime() {
		return 10_000;
	}

}
