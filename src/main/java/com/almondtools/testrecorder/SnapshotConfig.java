package com.almondtools.testrecorder;

import java.util.List;
import java.util.function.Consumer;

public interface SnapshotConfig {

	Consumer<GeneratedSnapshot> getConsumer();

	long getTimeoutInMillis();

	List<String> getPackages();

}
