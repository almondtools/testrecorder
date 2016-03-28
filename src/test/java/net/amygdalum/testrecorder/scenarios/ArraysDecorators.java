package net.amygdalum.testrecorder.scenarios;

import java.util.Arrays;
import java.util.List;

import net.amygdalum.testrecorder.Snapshot;

public class ArraysDecorators {

	public ArraysDecorators() {
	}

	@Snapshot
	public List<Object> asList(Object... objects) {
		return Arrays.asList(objects);
		
	}

}