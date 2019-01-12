package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class This {

	private String content;

	public This(String content) {
		this.content = content;
	}

	@Recorded
	public String getContent() {
		return content;
	}
	
	@Recorded
	public void setContent(String content) {
		this.content = content;
	}
	
}