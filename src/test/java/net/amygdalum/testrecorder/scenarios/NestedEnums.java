package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class NestedEnums {

	@Snapshot
	public void inc(NestedEnum nestedEnum) {
		nestedEnum.inc();
	}

	@Snapshot
	public String name(Enum<?> unknownEnum) {
		return unknownEnum.name();
	}

	@Snapshot
	public String toString(Object o) {
		return o.toString();
	}
}
