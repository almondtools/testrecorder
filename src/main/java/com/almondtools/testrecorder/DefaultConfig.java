package com.almondtools.testrecorder;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.almondtools.testrecorder.generator.TestGenerator;

public class DefaultConfig implements SnapshotConfig {

	@Override
	public Consumer<GeneratedSnapshot> getConsumer() {
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
