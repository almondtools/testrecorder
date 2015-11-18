package com.almondtools.testrecorder;

import java.util.Collections;
import java.util.List;

public class DefaultConfig implements SnapshotConfig {

	@Override
	public SnapshotConsumer getSnapshotConsumer() {
		return new TestGenerator();
	}
	
	@Override
	public long getTimeoutInMillis() {
		return 100_000;
	}

	@Override
	public List<String> getPackages() {
		return Collections.emptyList();
	}
}
