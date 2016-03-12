package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class GenericDataTypes {

	public GenericDataTypes() {
	}

	@Snapshot
	public StringBuilder objects(StringBuilder buffer, int i) {
		buffer.append(i);
		return buffer;
	}

}