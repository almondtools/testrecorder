package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class NestedEnums {

	@Recorded
	public void inc(NestedEnum nestedEnum) {
		nestedEnum.inc();
	}

	@Recorded
	public String name(Enum<?> unknownEnum) {
		return unknownEnum.name();
	}

	@Recorded
	public String toString(Object o) {
		return o.toString();
	}
}
