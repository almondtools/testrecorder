package net.amygdalum.testrecorder;

import java.util.List;

import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.SerializationProfile;

public interface TestRecorderAgentConfig extends SerializationProfile {

	SnapshotConsumer getSnapshotConsumer();

	List<Classes> getClasses();

}
