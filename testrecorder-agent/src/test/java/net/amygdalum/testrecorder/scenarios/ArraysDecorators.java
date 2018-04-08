package net.amygdalum.testrecorder.scenarios;

import java.util.Arrays;
import java.util.List;

import net.amygdalum.testrecorder.profile.Recorded;

public class ArraysDecorators {

	public ArraysDecorators() {
	}

	@Recorded
	public List<Object> asList(Object... objects) {
		return Arrays.asList(objects);
		
	}

	@Recorded
	public void consume(List<Object> list) {
	}
}