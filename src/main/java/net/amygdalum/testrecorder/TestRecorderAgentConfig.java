package net.amygdalum.testrecorder;

import java.util.List;

public interface TestRecorderAgentConfig extends SerializationProfile {

	SnapshotConsumer getSnapshotConsumer();

	long getTimeoutInMillis();

	List<Classes> getClasses();

}
