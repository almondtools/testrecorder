package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class GenericDataTypes {

	public GenericDataTypes() {
	}

	@Snapshot
	public StringBuilder objects(StringBuilder buffer, int i) {
		buffer.append(i);
		return buffer;
	}

}