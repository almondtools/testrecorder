package com.almondtools.testrecorder.generator;

import com.almondtools.testrecorder.ValueSnapshot;

public interface ValueSnapshotConsumer {

	void accept(ValueSnapshot snapshot);
}
