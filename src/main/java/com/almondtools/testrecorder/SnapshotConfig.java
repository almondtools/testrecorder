package com.almondtools.testrecorder;

import java.util.List;

public interface SnapshotConfig {

	SnapshotConsumer getSnapshotConsumer();

	long getTimeoutInMillis();

	List<String> getPackages();

}
