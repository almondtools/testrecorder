package com.almondtools.testrecorder;

import java.util.List;

import com.almondtools.testrecorder.generator.MethodSnapshotConsumer;
import com.almondtools.testrecorder.generator.ValueSnapshotConsumer;

public interface SnapshotConfig {

	MethodSnapshotConsumer getMethodConsumer();

	ValueSnapshotConsumer getValueConsumer();

	long getTimeoutInMillis();

	List<String> getPackages();

}
