package net.amygdalum.testrecorder.scenarios;

import java.util.List;

import net.amygdalum.testrecorder.profile.Recorded;

public class GenericConstructor {

	private List<String> buffer;

	public GenericConstructor(List<String> buffer) {
		this.buffer = buffer;
	}

	@Recorded
	public void add(String value) {
		buffer.add(value);
	}
	
	public List<String> getBuffer() {
		return buffer;
	}
	
	
}