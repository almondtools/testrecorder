package net.amygdalum.testrecorder.profile;

import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;
import net.amygdalum.testrecorder.extensionpoint.ExtensionStrategy;

@ExtensionPoint(strategy=ExtensionStrategy.OVERRIDING)
public interface PerformanceProfile {

	long getTimeoutInMillis();

	long getIdleTime();

}
