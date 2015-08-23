package com.almondtools.invivoderived.scenarios;

import com.almondtools.invivoderived.analyzer.Snapshot;

public class GenericDataTypes {

	public GenericDataTypes() {
	}

	@Snapshot
	public StringBuilder objects(StringBuilder buffer, int i) {
		buffer.append(i);
		return buffer;
	}

}