package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class NestedEnums {

	@Snapshot
	public void inc(NestedEnum nestedEnum) {
		nestedEnum.inc();
	}
}
