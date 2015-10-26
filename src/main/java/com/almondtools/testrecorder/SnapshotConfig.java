package com.almondtools.testrecorder;

import java.util.List;

public interface SnapshotConfig {

	MethodSnapshotConsumer getMethodConsumer();

	ValueSnapshotConsumer getValueConsumer();

	long getTimeoutInMillis();

	List<String> getPackages();

}
