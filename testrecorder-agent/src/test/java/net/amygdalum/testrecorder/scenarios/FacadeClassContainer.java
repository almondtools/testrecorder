package net.amygdalum.testrecorder.scenarios;

import java.io.IOException;

import net.amygdalum.testrecorder.profile.Recorded;

public class FacadeClassContainer {

	private FacadeClassExample facade;
	
	public FacadeClassContainer(String... values) throws IOException {
		this.facade = new FacadeClassExample(values);
	}

	@Recorded
	public String readFromFacade() throws IOException {
		return facade.read();
	}
	
	@Recorded
	public void writeToFacade(String value) throws IOException {
		facade.write(value);
	}
	
}