package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class GenericDataTypes<T> {

	public GenericDataTypes() {
	}

	@Recorded
	public StringBuilder objects(StringBuilder buffer, T t) {
		buffer.append(t.toString());
		return buffer;
	}

}