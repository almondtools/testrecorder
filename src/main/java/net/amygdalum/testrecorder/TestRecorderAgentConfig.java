package net.amygdalum.testrecorder;

import java.util.List;

public interface TestRecorderAgentConfig extends SerializationProfile {

	SnapshotConsumer getSnapshotConsumer();

	List<Classes> getClasses();

}
