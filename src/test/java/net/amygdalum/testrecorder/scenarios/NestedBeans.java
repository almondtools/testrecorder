package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.util.testobjects.NestedAbstract;

public class NestedBeans {

	@Recorded
	public static int extractId(NestedAbstract nested) {
		return nested.getId();
	}

}
