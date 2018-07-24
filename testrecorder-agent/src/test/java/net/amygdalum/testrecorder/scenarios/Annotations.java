package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.hints.AnnotateGroupExpression;
import net.amygdalum.testrecorder.hints.AnnotateTimestamp;
import net.amygdalum.testrecorder.profile.Recorded;

public class Annotations {

	private String id;

	public Annotations(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	@Recorded
	@AnnotateTimestamp
	public int withTimeStamp(String s) {
		return Integer.parseInt(s);
	}

	@Recorded
	@AnnotateGroupExpression(expression=".id")
	public int withGroup(String s) {
		return Integer.parseInt(s);
	}
}