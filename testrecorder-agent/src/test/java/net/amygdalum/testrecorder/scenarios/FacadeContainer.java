package net.amygdalum.testrecorder.scenarios;

import java.io.IOException;

import net.amygdalum.testrecorder.profile.Facade;
import net.amygdalum.testrecorder.profile.Recorded;

public class FacadeContainer {

	@Facade
	private FacadeExample facade;
	
	public FacadeContainer(String... values) throws IOException {
		this.facade = new FacadeExample(values);
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