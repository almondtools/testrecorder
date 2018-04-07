package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class GenericDataTypes {

	public GenericDataTypes() {
	}

	@Recorded
	public StringBuilder objects(StringBuilder buffer, int i) {
		buffer.append(i);
		return buffer;
	}

}