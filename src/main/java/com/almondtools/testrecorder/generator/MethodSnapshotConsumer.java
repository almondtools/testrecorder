package com.almondtools.testrecorder.generator;

import com.almondtools.testrecorder.MethodSnapshot;

public interface MethodSnapshotConsumer {

	void accept(MethodSnapshot snapshot);
}
